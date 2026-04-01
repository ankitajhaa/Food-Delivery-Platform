package com.fooddelivery.orderservice.dto;

public record OrderResponse(Long id, Long restaurantId, String status) {
}
