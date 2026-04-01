package com.fooddelivery.restaurantservice.mapper;

import com.fooddelivery.restaurantservice.dto.RestaurantResponse;
import com.fooddelivery.restaurantservice.model.Restaurant;

public class RestaurantMapper {

    public static RestaurantResponse toResponse(Restaurant r) {
        return new RestaurantResponse(r.getId(), r.getName());
    }
}
