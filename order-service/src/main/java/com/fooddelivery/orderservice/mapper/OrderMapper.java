package com.fooddelivery.orderservice.mapper;

import com.fooddelivery.orderservice.dto.OrderRequest;
import com.fooddelivery.orderservice.dto.OrderResponse;
import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.orderservice.model.OrderStatus;

public class OrderMapper {

    public static Order toEntity(OrderRequest req) {
        Order o = new Order();
        o.setRestaurantId(req.restaurantId());
        o.setStatus(OrderStatus.CREATED);
        return o;
    }

    public static OrderResponse toResponse(Order o) {
        return new OrderResponse(o.getId(), o.getRestaurantId(), o.getStatus().name());
    }
}
