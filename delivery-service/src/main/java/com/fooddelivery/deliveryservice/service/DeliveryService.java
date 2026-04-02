package com.fooddelivery.deliveryservice.service;

import com.fooddelivery.deliveryservice.client.RiderClient;
import com.fooddelivery.deliveryservice.client.RiderResponse;
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

    public DeliveryService(DeliveryRepository deliveryRepository, RiderClient riderClient, DeliveryEventPublisher eventPublisher) {
        this.deliveryRepository = deliveryRepository;
        this.riderClient = riderClient;
        this.eventPublisher = eventPublisher;
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
}
