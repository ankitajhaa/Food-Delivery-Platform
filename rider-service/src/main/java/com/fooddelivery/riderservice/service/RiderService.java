package com.fooddelivery.riderservice.service;

import com.fooddelivery.riderservice.dto.RiderResponse;
import com.fooddelivery.riderservice.mapper.RiderMapper;
import com.fooddelivery.riderservice.model.Rider;
import com.fooddelivery.riderservice.repository.RiderRepository;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RiderService {

    private final RiderRepository riderRepository;

    private static final Logger log = LoggerFactory.getLogger(RiderService.class);

    public RiderService(RiderRepository riderRepository) {
        this.riderRepository = riderRepository;
    }

    public RiderResponse getRider(Long id) {
        return riderRepository.findById(id)
                .map(RiderMapper::toResponse)
                .orElseThrow(() -> new RuntimeException("Rider not found with id: " + id));
    }

    public RiderResponse assignRider(Long deliveryId) {
        return getFirstAvailableRider();
    }

    public RiderResponse getFirstAvailableRider() {
        log.info("Finding first available rider");
        Rider rider = riderRepository.findFirstByAvailableTrue()
                .orElseThrow(() -> new RuntimeException("No riders available"));

        rider.setAvailable(false);
        riderRepository.save(rider);

        log.info("Assigned rider: id={}, name={}", rider.getId(), rider.getName());

        return RiderMapper.toResponse(rider);
    }
}
