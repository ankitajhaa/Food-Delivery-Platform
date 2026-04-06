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

    public DeliveryResponse createDelivery(DeliveryRequest request) {
        Delivery delivery = DeliveryMapper.toEntity(request);
        Delivery saved = deliveryRepository.save(delivery);

        try {
            RiderResponse rider = riderClient.getRider(1L);
            if (rider != null) {
                saved.setRiderId(rider.getId());
                saved.setStatus(DeliveryStatus.ASSIGNED);
                deliveryRepository.save(saved);
                log.info("Rider assigned synchronously: riderId = {}", rider.getId());
            }
        } catch (Exception e) {
            log.warn("Sync rider assignment failed, falling back to async: {}", e.getMessage());
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

        OrderResponse order = orderClient.getOrder(delivery.getOrderId());
        if (order != null) {
            details.setOrderStatus(order.getStatus());
            details.setRestaurantId(order.getRestaurantId());
        }

        if (details.getRestaurantId() != null) {
            RestaurantResponse restaurant = restaurantClient.getRestaurant(details.getRestaurantId());
            if (restaurant != null) {
                details.setRestaurantName(restaurant.getName());
            }
        }

        if (delivery.getRiderId() != null) {
            RiderResponse rider = riderClient.getRider(delivery.getRiderId());
            if (rider != null) {
                details.setRiderName(rider.getName());
                details.setRiderZone(rider.getZone());
            }
        }

        return details;
    }

    public DeliveryResponse updateDelivery(Long id, String status) {
        Delivery delivery = deliveryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Delivery not found: " + id));
        delivery.setStatus(DeliveryStatus.valueOf(status));
        return DeliveryMapper.toResponse(deliveryRepository.save(delivery));
    }
}
