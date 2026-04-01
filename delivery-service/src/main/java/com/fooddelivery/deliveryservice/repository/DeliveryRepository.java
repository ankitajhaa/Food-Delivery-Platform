package com.fooddelivery.deliveryservice.repository;

import com.fooddelivery.deliveryservice.model.Delivery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeliveryRepository extends JpaRepository<Delivery, Long> {
}
