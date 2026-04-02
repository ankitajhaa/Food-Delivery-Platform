package com.fooddelivery.analyticsservice.messaging;

public class OrderCreatedEvent {

    private Long orderId;

    private Long restaurantId;

    public OrderCreatedEvent() {}

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(Long restaurantId) {
        this.restaurantId = restaurantId;
    }
}
