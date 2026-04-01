package com.fooddelivery.restaurantservice.repository;

import com.fooddelivery.restaurantservice.model.Restaurant;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
}
