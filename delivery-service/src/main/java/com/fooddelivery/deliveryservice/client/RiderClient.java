package com.fooddelivery.deliveryservice.client;

import com.fooddelivery.deliveryservice.client.RiderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "rider-service", fallback = RiderClientFallback.class)
public interface RiderClient {

    @GetMapping("/riders/{id}")
    RiderResponse getRider(@PathVariable Long id);
}