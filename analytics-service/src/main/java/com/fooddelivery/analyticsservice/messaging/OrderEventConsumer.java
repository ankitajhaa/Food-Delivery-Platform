package com.fooddelivery.analyticsservice.messaging;

import com.fooddelivery.analyticsservice.model.AnalyticsRecord;
import com.fooddelivery.analyticsservice.repository.AnalyticsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
public class OrderEventConsumer {

    private static final Logger log = LoggerFactory.getLogger(OrderEventConsumer.class);

    private final AnalyticsRepository analyticsRepository;

    public OrderEventConsumer(AnalyticsRepository analyticsRepository) {
        this.analyticsRepository = analyticsRepository;
    }

    @RabbitListener(queues = "analytics.queue")
    public void handleOrderCreated(OrderCreatedEvent event) {
        try {
            log.info("Analytics received OrderCreatedEvent for orderId={}", event.getOrderId());
            AnalyticsRecord record = new AnalyticsRecord();
            record.setMetricType("ORDER_CREATED");
            record.setValue(event.getOrderId());
            analyticsRepository.save(record);
            log.info("Saved analytics record for orderId={}", event.getOrderId());
        } catch (Exception e) {
            log.error("Failed to process analytics event: {}", e.getMessage());
        }
    }
}
