package com.fooddelivery.deliveryservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "restaurant-service", fallback = RestaurantClientFallback.class)
public interface RestaurantClient {

    @GetMapping("/restaurants/{id}")
    RestaurantResponse getRestaurant(@PathVariable Long id);
}
