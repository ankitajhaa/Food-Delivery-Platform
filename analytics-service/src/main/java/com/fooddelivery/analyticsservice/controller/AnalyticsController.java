package com.fooddelivery.analyticsservice.controller;

import com.fooddelivery.analyticsservice.dto.AnalyticsResponse;
import com.fooddelivery.analyticsservice.model.AnalyticsRecord;
import com.fooddelivery.analyticsservice.service.AnalyticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService){
        this.analyticsService = analyticsService;
    }

    @GetMapping
    public ResponseEntity<List<AnalyticsResponse>> getAllMetrics() {
        return ResponseEntity.ok(analyticsService.getAllMetrics());
    }

    @GetMapping("/type")
    public ResponseEntity<List<AnalyticsResponse>> getMetricsByType(@PathVariable String type) {
        return ResponseEntity.ok(analyticsService.getMetricsByType(type));
    }
}
