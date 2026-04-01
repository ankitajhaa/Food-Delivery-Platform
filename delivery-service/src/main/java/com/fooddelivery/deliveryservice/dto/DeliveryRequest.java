package com.fooddelivery.deliveryservice.dto;

import jakarta.validation.constraints.NotNull;

public record DeliveryRequest(@NotNull Long orderId) {
}
