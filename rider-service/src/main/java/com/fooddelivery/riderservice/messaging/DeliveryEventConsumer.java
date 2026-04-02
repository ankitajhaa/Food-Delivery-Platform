package com.fooddelivery.riderservice.messaging;

import com.fooddelivery.riderservice.service.RiderService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class DeliveryEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(DeliveryEventConsumer.class);

    private final RiderService riderService;

    public DeliveryEventConsumer(RiderService riderService) {
        this.riderService = riderService;
    }

    @RabbitListener(queues = "rider.queue")
    public void handleDeliveryCreated(DeliveryCreatedEvent event) {
        try {
            log.info("Rider Service received DeliveryCreatedEvent: deliveryId = {}, orderId = {}", event.getDeliveryId(), event.getOrderId());
            riderService.assignRider(event.getDeliveryId());
            log.info("Rider assigned to deliveryId = {}", event.getDeliveryId());
        }
        catch (Exception e) {
            log.error("Failed to assign rider for deliveryId = {}: {}", event.getDeliveryId(), e.getMessage());
        }
    }
}
