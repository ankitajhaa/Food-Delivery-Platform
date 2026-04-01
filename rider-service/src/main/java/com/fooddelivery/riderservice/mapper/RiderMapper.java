package com.fooddelivery.riderservice.mapper;

import com.fooddelivery.riderservice.dto.RiderResponse;
import com.fooddelivery.riderservice.model.Rider;

public class RiderMapper {

    public static RiderResponse toResponse(Rider r) {
        return new RiderResponse(r.getId(), r.getName(), r.getZone());
    }
}
