package com.fooddelivery.deliveryservice.controller;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/deliveries")
public class SimulationController {

    private static final Logger log = LoggerFactory.getLogger(SimulationController.class);

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    public SimulationController(CircuitBreakerRegistry circuitBreakerRegistry) {
        this.circuitBreakerRegistry = circuitBreakerRegistry;
    }

    @PostMapping("/simulate-failure")
    public ResponseEntity<Map<String, String>> simulateFailure() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("RiderClient#getRider(Long)");
        cb.transitionToOpenState();
        log.warn("Circuit breaker manually forced OPEN");
        return ResponseEntity.ok(Map.of(
                "circuitBreaker", "RiderClient#getRider(Long)",
                "state", cb.getState().name(),
                "message", "Circuit breaker forced OPEN - rider-service calls will be rejected"
        ));
    }

    @PostMapping("/simulate-recovery")
    public ResponseEntity<Map<String, String>> simulateRecovery() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("RiderClient#getRider(Long)");
        cb.transitionToClosedState();
        log.info("Circuit breaker manually reset to CLOSED");
        return ResponseEntity.ok(Map.of(
                "circuitBreaker", "RiderClient#getRider(Long)",
                "state", cb.getState().name(),
                "message", "Circuit breaker reset to CLOSED - rider-service calls will resume"
        ));
    }

    @GetMapping("/circuit-breaker/status")
    public ResponseEntity<Map<String, String>> getStatus() {
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("RiderClient#getRider(Long)");
        CircuitBreaker.Metrics metrics = cb.getMetrics();
        return ResponseEntity.ok(Map.of(
                "circuitBreaker", "RiderClient#getRider(Long)",
                "state", cb.getState().name(),
                "failureRate", metrics.getFailureRate() + "%",
                "bufferedCalls", String.valueOf(metrics.getNumberOfBufferedCalls())
        ));
    }
}
