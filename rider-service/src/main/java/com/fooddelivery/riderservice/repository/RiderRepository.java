package com.fooddelivery.riderservice.repository;

import com.fooddelivery.riderservice.model.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiderRepository extends JpaRepository<Rider, Long> {
}
