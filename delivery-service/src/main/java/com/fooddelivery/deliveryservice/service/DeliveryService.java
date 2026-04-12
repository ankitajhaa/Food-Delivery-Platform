package com.fooddelivery.deliveryservice.service;

import com.fooddelivery.deliveryservice.client.*;
import com.fooddelivery.deliveryservice.dto.DeliveryDetailsResponse;
import com.fooddelivery.deliveryservice.dto.DeliveryRequest;
import com.fooddelivery.deliveryservice.dto.DeliveryResponse;
import com.fooddelivery.deliveryservice.mapper.DeliveryMapper;
import com.fooddelivery.deliveryservice.messaging.DeliveryCreatedEvent;
import com.fooddelivery.deliveryservice.messaging.DeliveryEventPublisher;
import com.fooddelivery.deliveryservice.model.Delivery;
import com.fooddelivery.deliveryservice.model.DeliveryStatus;
import com.fooddelivery.deliveryservice.repository.DeliveryRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DeliveryService {

    private static final Logger log = LoggerFactory.getLogger(DeliveryService.class);

    private final DeliveryRepository deliveryRepository;

    private final RiderClient riderClient;

    private final DeliveryEventPublisher eventPublisher;

    private final OrderClient orderClient;

    private final RestaurantClient restaurantClient;

    public DeliveryService(DeliveryRepository deliveryRepository, RiderClient riderClient, DeliveryEventPublisher eventPublisher, OrderClient orderClient, RestaurantClient restaurantClient) {
        this.deliveryRepository = deliveryRepository;
        this.riderClient = riderClient;
        this.eventPublisher = eventPublisher;
        this.orderClient = orderClient;
        this.restaurantClient = restaurantClient;
    }

    @Transactional
    public DeliveryResponse createDelivery(DeliveryRequest request) {
        log.info("Validating orderId={}", request.orderId());
        try {
            orderClient.getOrder(request.orderId());
        } catch (Exception e) {
            throw new IllegalArgumentException("Order not found with id: " + request.orderId());
        }

        Delivery delivery = DeliveryMapper.toEntity(request);
        Delivery saved = deliveryRepository.save(delivery);

        RiderResponse rider;
        try {
            rider = riderClient.getFirstAvailableRider();
        } catch (Exception e) {
            log.warn("Rider service unavailable: {}", e.getMessage());
            rider = null;
        }
        if (rider != null) {
            saved.setRiderId(rider.getId());
            saved.setStatus(DeliveryStatus.ASSIGNED);
            deliveryRepository.save(saved);
            log.info("Rider assigned: riderId={}", rider.getId());
        }
        else {
            log.info("No rider assigned, will retry asynchronously");
        }

        eventPublisher.publishDeliveryCreated(
                new DeliveryCreatedEvent(saved.getId(), saved.getOrderId())
        );
        return DeliveryMapper.toResponse(saved);
    }

    public DeliveryResponse getDelivery(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found: " + id));
        return DeliveryMapper.toResponse(delivery);
    }

    public DeliveryDetailsResponse getDeliveryDetails(Long id) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found: " + id));

        DeliveryDetailsResponse details = new DeliveryDetailsResponse();
        details.setDeliveryId(delivery.getId());
        details.setDeliveryStatus(delivery.getStatus().name());
        details.setOrderId(delivery.getOrderId());
        details.setRiderId(delivery.getRiderId());

        try {
            OrderResponse order = orderClient.getOrder(delivery.getOrderId());
            if (order != null) {
                details.setOrderStatus(order.getStatus());
                details.setRestaurantId(order.getRestaurantId());
            }
        } catch (Exception e) {
            log.warn("Order service failed: {}", e.getMessage());
        }

        try {
            if (details.getRestaurantId() != null) {
                RestaurantResponse restaurant = restaurantClient.getRestaurant(details.getRestaurantId());
                if (restaurant != null) {
                    details.setRestaurantName(restaurant.getName());
                }
            }
        } catch (Exception e) {
            log.warn("Restaurant service failed: {}", e.getMessage());
        }

        try {
            if (delivery.getRiderId() != null) {
                RiderResponse rider = riderClient.getRider(delivery.getRiderId());
                if (rider != null) {
                    details.setRiderName(rider.getName());
                    details.setRiderZone(rider.getZone());
                }
            }
        } catch (Exception e) {
            log.warn("Rider service failed: {}", e.getMessage());
        }

        return details;
    }

    @Transactional
    public DeliveryResponse updateDelivery(Long id, String status) {
        log.info("Updating delivery id={} to status={}", id, status);
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found: " + id));
        DeliveryStatus current = delivery.getStatus();
        DeliveryStatus next;
        try {
            next = DeliveryStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }
        if (!isValidDeliveryTransition(current, next)) {
            throw new IllegalArgumentException("Invalid status transition: " + current + " -> " + next);
        }
        delivery.setStatus(next);
        log.info("Delivery id={} updated to status={}", id, next);
        return DeliveryMapper.toResponse(deliveryRepository.save(delivery));
    }

    private boolean isValidDeliveryTransition(DeliveryStatus current, DeliveryStatus next) {
        return (current == DeliveryStatus.PENDING && next == DeliveryStatus.ASSIGNED) ||
                (current == DeliveryStatus.ASSIGNED && next == DeliveryStatus.DONE);
    }
}
