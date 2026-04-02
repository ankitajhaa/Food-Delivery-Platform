package com.fooddelivery.riderservice.messaging;

public class DeliveryCreatedEvent {

    private Long deliveryId;

    private Long orderId;

    public DeliveryCreatedEvent() {}

    public DeliveryCreatedEvent(Long deliveryId, Long orderId) {
        this.deliveryId = deliveryId;
        this.orderId = orderId;
    }

    public Long getDeliveryId() {
        return deliveryId;
    }

    public void setDeliveryId(Long deliveryId) {
        this.deliveryId = deliveryId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }
}
