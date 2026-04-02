package com.fooddelivery.analyticsservice.repository;

import com.fooddelivery.analyticsservice.model.AnalyticsRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnalyticsRepository extends JpaRepository<AnalyticsRecord, Long> {

    List<AnalyticsRecord> findByMetricType(String metricType);

}
