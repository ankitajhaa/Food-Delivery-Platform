package com.fooddelivery.deliveryservice.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RiderClientFallback implements RiderClient {

    private static final Logger log = LoggerFactory.getLogger(RiderClientFallback.class);

    @Override
    public RiderResponse getRider(Long id) {
        log.warn("Rider-Service unavailable, using fallback for riderId={}", id);
        throw new RuntimeException("Rider service unavailable");
    }

    @Override
    public RiderResponse getFirstAvailableRider() {
        log.warn("Rider-Service unavailable, fallback for getFirstAvailableRider");
        throw new RuntimeException("Rider service unavailable");
    }
}