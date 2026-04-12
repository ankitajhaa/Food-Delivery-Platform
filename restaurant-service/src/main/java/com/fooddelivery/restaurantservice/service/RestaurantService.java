package com.fooddelivery.restaurantservice.service;

import com.fooddelivery.restaurantservice.dto.RestaurantResponse;
import com.fooddelivery.restaurantservice.mapper.RestaurantMapper;
import com.fooddelivery.restaurantservice.repository.RestaurantRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class RestaurantService {

    private static final Logger log = LoggerFactory.getLogger(RestaurantService.class);

    private final RestaurantRepository restaurantRepository;

    public RestaurantService(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    public RestaurantResponse getRestaurant(Long id) {
        return restaurantRepository.findById(id)
                .map(RestaurantMapper::toResponse)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurant not found: " + id));
    }

    public void processOrder(Long orderId, Long restaurantId) {
        restaurantRepository.findById(restaurantId).ifPresentOrElse(
                restaurant -> log.info("Restaurant '{}' (id={}) is now processing order id={}",
                        restaurant.getName(), restaurantId, orderId),
                () -> log.warn("Restaurant id={} not found while processing order id={}",
                        restaurantId, orderId)
        );
    }
}
