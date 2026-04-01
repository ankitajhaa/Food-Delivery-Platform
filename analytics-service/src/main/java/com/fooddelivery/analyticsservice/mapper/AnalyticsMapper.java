package com.fooddelivery.analyticsservice.mapper;

import com.fooddelivery.analyticsservice.dto.AnalyticsResponse;
import com.fooddelivery.analyticsservice.model.AnalyticsRecord;

public class AnalyticsMapper {

    public static AnalyticsResponse toResponse(AnalyticsRecord a) {
        return new AnalyticsResponse(a.getId(), a.getMetricType(), a.getValue());
    }
}
