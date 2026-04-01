package com.fooddelivery.deliveryservice.mapper;

import com.fooddelivery.deliveryservice.dto.DeliveryRequest;
import com.fooddelivery.deliveryservice.dto.DeliveryResponse;
import com.fooddelivery.deliveryservice.model.Delivery;
import com.fooddelivery.deliveryservice.model.DeliveryStatus;

public class DeliveryMapper {

    public static Delivery toEntity(DeliveryRequest req) {
        Delivery d = new Delivery();
        d.setOrderId(req.orderId());
        d.setStatus(DeliveryStatus.PENDING);
        return d;
    }

    public static DeliveryResponse toResponse(Delivery d) {
        return new DeliveryResponse(d.getId(), d.getOrderId(), d.getRiderId(), d.getStatus().name());
    }
}
