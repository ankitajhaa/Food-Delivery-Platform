package com.fooddelivery.deliveryservice.dto;

public record DeliveryResponse(Long id, Long orderId, Long riderId, String status) {
}
