package com.fooddelivery.orderservice.messaging;

import com.fooddelivery.orderservice.config.RabbitMQConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
public class OrderEventPublisher {

    private static final Logger log = LoggerFactory.getLogger(OrderEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public OrderEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishOrderCreated(OrderCreatedEvent event) {
        try {
            rabbitTemplate.convertAndSend(
                    RabbitMQConfig.ORDERS_EXCHANGE,
                    RabbitMQConfig.ORDER_ROUTING_KEY,
                    event
            );
            log.info("Published OrderCreatedEvent for orderId={}", event.getOrderId());
        }
        catch (Exception e) {
            log.error("Failed to publish OrderCreatedEvent for orderId={}: {}", event.getOrderId(), e.getMessage());
        }
    }
}
