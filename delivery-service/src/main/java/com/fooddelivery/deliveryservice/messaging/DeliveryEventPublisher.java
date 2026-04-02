package com.fooddelivery.deliveryservice.messaging;

import com.fooddelivery.deliveryservice.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class DeliveryEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(DeliveryEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public DeliveryEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishDeliveryCreated(DeliveryCreatedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.DELIVERY_EXCHANGE,
                    RabbitMQConfig.DELIVERY_ROUTING_KEY,
                    event
            );
            log.info("Published DeliveryCreatedEvent for deliveryId = {}", event.getDeliveryId());
        }
        catch (Exception e) {
            log.error("Failed to publish DeliveryCreatedEvent: {}", e.getMessage());
        }
    }
}
