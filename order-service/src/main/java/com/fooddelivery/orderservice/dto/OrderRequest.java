package com.fooddelivery.orderservice.dto;

import jakarta.validation.constraints.NotNull;

public record OrderRequest(@NotNull Long restaurantId) {
}
