package com.fooddelivery.restaurantservice.messaging;

import com.fooddelivery.restaurantservice.service.RestaurantService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);
    private final RestaurantService restaurantService;

    public OrderEventConsumer(RestaurantService restaurantService) {
        this.restaurantService = restaurantService;
    }

    @RabbitListener(queues = "restaurant.queue")
    public void handleOrderCreated(OrderCreatedEvent event) {
        try {
            log.info("Restaurant-Service: received order id={} for restaurantId={}", event.getOrderId(), event.getRestaurantId());
            restaurantService.processOrder(event.getOrderId(), event.getRestaurantId());
            log.info("Restaurant-Service: processing order id={} successfully processed", event.getOrderId());
        } catch (Exception e) {
            log.error("Restaurant-Service: failed to process order id={}: {}", event.getOrderId(), e.getMessage());
        }
    }
}
