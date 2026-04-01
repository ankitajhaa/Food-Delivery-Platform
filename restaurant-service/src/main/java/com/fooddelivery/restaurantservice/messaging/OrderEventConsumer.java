package com.fooddelivery.restaurantservice.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    @RabbitListener(queues = "restaurant.queue")
    public void handleOrderCreated(OrderCreatedEvent event) {
        try {
            log.info("Restaurant-Service received OrderCreatedEvent: orderId = {}, restaurantId = {}", event.getOrderId(), event.getRestaurantId());

            log.info("Order {} accepted by restaurant {}", event.getOrderId(), event.getRestaurantId());
        }
        catch (Exception e) {
            log.error("Failed to process OrderCreatedEvent: {}", e.getMessage());
        }
    }
}
