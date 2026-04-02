package com.fooddelivery.analyticsservice.service;

import com.fooddelivery.analyticsservice.dto.AnalyticsResponse;
import com.fooddelivery.analyticsservice.mapper.AnalyticsMapper;
import com.fooddelivery.analyticsservice.repository.AnalyticsRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AnalyticsService {

    private final AnalyticsRepository analyticsRepository;

    public AnalyticsService(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    public List<AnalyticsResponse> getAllMetrics() {
        return analyticsRepository.findAll().stream()
                .map(AnalyticsMapper::toResponse)
                .toList();
    }

    public List<AnalyticsResponse> getMetricsByType(String type) {
        return analyticsRepository.findByMetricType(type).stream()
                .map(AnalyticsMapper::toResponse)
                .toList();
    }
}
