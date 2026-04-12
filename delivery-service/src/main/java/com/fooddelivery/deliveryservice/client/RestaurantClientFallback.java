package com.fooddelivery.deliveryservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RestaurantClientFallback implements RestaurantClient {

    private static final Logger log = LoggerFactory.getLogger(RestaurantClientFallback.class);

    @Override
    public RestaurantResponse getRestaurant(Long id) {
        log.warn("Restaurant-Service unavailable, fallback for restaurantId={}", id);
        throw new RuntimeException("Restaurant service unavailable");
    }
}
