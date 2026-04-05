package com.fooddelivery.deliveryservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class OrderClientFallback {

    private static final Logger log = LoggerFactory.getLogger(OrderClientFallback.class);

    @Override
    public OrderResponse getOrder(Long id) {
        log.warn("Order-Service unavailable, using fallback for orderId={}", id);
        return null;
    }
}
