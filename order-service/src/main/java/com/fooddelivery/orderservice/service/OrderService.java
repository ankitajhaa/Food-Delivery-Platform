package com.fooddelivery.orderservice.service;

import com.fooddelivery.orderservice.OrderServiceApplication;
import com.fooddelivery.orderservice.dto.OrderRequest;
import com.fooddelivery.orderservice.dto.OrderResponse;
import com.fooddelivery.orderservice.mapper.OrderMapper;
import com.fooddelivery.orderservice.messaging.OrderCreatedEvent;
import com.fooddelivery.orderservice.messaging.OrderEventPublisher;
import com.fooddelivery.orderservice.model.Order;
import com.fooddelivery.orderservice.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private final OrderEventPublisher eventPublisher;

    public OrderService(OrderRepository orderRepository, OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    public OrderResponse createOrder(OrderRequest request) {
        Order order = OrderMapper.toEntity(request);
        Order saved = orderRepository.save(order);

        eventPublisher.publishOrderCreated(
                new OrderCreatedEvent(saved.getId(), saved.getRestaurantId())
        );

        return OrderMapper.toResponse(saved);
    }

    public OrderResponse getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found: " + id));
        return OrderMapper.toResponse(order);
    }
}
