package com.fooddelivery.orderservice.messaging;

public class OrderCreatedEvent {

    private String eventId = java.util.UUID.randomUUID().toString();

    private String eventType = "ORDER_CREATED";

    private Long orderId;

    private Long restaurantId;

    private String status;

    public OrderCreatedEvent() {}

    public OrderCreatedEvent(Long orderId, Long restaurantId, String status) {
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.status = status;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
