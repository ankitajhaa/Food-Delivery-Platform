package com.fooddelivery.riderservice.repository;

import com.fooddelivery.riderservice.model.Rider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RiderRepository extends JpaRepository<Rider, Long> {

    java.util.Optional<Rider> findFirstByAvailableTrue();
}
