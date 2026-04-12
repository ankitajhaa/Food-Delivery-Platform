package com.fooddelivery.orderservice.service;

import com.fooddelivery.orderservice.client.RestaurantClient;
import com.fooddelivery.orderservice.client.RestaurantResponse;
import com.fooddelivery.orderservice.dto.OrderRequest;
import com.fooddelivery.orderservice.dto.OrderResponse;
import com.fooddelivery.orderservice.mapper.OrderMapper;
import com.fooddelivery.orderservice.messaging.OrderCreatedEvent;
import com.fooddelivery.orderservice.messaging.OrderEventPublisher;
import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.orderservice.model.OrderStatus;
import com.fooddelivery.orderservice.repository.OrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final OrderRepository orderRepository;
    private final OrderEventPublisher eventPublisher;
    private final RestaurantClient restaurantClient;

    public OrderService(OrderRepository orderRepository, OrderEventPublisher eventPublisher, RestaurantClient restaurantClient) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
        this.restaurantClient = restaurantClient;
    }

    @Transactional
    public OrderResponse createOrder(OrderRequest request) {
        try {
            RestaurantResponse restaurant = restaurantClient.getRestaurant(request.restaurantId());
            if (restaurant == null) {
                throw new IllegalArgumentException("Restaurant not found: " + request.restaurantId());
            }
            log.info("Restaurant {} is processing order", request.restaurantId());
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Restaurant service unavailable: {}", e.getMessage());
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE, "Restaurant service unavailable"
            );
        }

        Order order = OrderMapper.toEntity(request);
        Order saved = orderRepository.save(order);
        eventPublisher.publishOrderCreated(
                new OrderCreatedEvent(saved.getId(), saved.getRestaurantId(), "CREATED")
        );
        return OrderMapper.toResponse(saved);
    }

    public OrderResponse getOrder(Long id) {
        log.info("Fetching order id={}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        return OrderMapper.toResponse(order);
    }

    public List<OrderResponse> getAllOrders() {
        log.info("Fetching all orders");
        return orderRepository.findAll().stream()
                .map(OrderMapper::toResponse)
                .toList();
    }

    @Transactional
    public OrderResponse updateOrder(Long id, String status) {
        log.info("Updating order id={} to status={}", id, status);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + id));
        OrderStatus current = order.getStatus();
        OrderStatus next;
        try {
            next = OrderStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status value: " + status);
        }
        if (!isValidTransition(current, next)) {
            throw new IllegalArgumentException("Invalid status transition: " + current + " -> " + next);
        }
        order.setStatus(next);
        Order updated = orderRepository.save(order);
        log.info("Order id={} updated to status={}", id, updated.getStatus());
        return OrderMapper.toResponse(updated);
    }

    private boolean isValidTransition(OrderStatus current, OrderStatus next) {
        return (current == OrderStatus.CREATED && next == OrderStatus.ACCEPTED) ||
                (current == OrderStatus.ACCEPTED && next == OrderStatus.DELIVERED);
    }
}