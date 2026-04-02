package com.fooddelivery.riderservice.service;

import com.fooddelivery.riderservice.dto.RiderResponse;
import com.fooddelivery.riderservice.mapper.RiderMapper;
import com.fooddelivery.riderservice.repository.RiderRepository;
import org.springframework.stereotype.Service;

@Service
public class RiderService {

    private final RiderRepository riderRepository;

    public RiderService(RiderRepository riderRepository) {
        this.riderRepository = riderRepository;
    }

    public RiderResponse getRider(Long id) {
        return riderRepository.findById(id)
                .map(RiderMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Rider not found with id: " + id));
    }

    public RiderResponse assignRider(Long deliveryId) {
        return riderRepository.findAll()
                .stream()
                .findFirst()
                .map(rider -> {
                    return RiderMapper.toResponse(rider);
                })
                .orElseThrow(() -> new RuntimeException("No riders available"));
    }
}
