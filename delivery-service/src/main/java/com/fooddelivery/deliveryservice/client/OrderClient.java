package com.fooddelivery.deliveryservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "order-service", fallback = OrderClientFallback.class)
public class OrderClient {

    @GetMapping("/orders/{id}")
    OrderResponse getOrder(@PathVariable Long id);
}
