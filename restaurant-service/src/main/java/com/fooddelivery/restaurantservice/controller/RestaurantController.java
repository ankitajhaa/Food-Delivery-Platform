package com.fooddelivery.restaurantservice.controller;

import com.fooddelivery.restaurantservice.dto.RestaurantResponse;
import com.fooddelivery.restaurantservice.service.RestaurantService;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/restaurants")
public class RestaurantController {

    private final RestaurantService restaurantService;
    private final Environment environment;

    public RestaurantController(RestaurantService restaurantService, Environment environment) {
        this.restaurantService = restaurantService;
        this.environment = environment;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RestaurantResponse> getRestaurant(@PathVariable Long id) {
        return ResponseEntity.ok(restaurantService.getRestaurant(id));
    }

    @GetMapping("/instance-info")
    public ResponseEntity<Map<String, String>> getInstanceInfo() {
        return ResponseEntity.ok(Map.of(
                "service", "restaurant-service",
                "port", environment.getProperty("local.server.port", "unknown"),
                "instanceId", environment.getProperty("server.port", "unknown")
        ));
    }
}
