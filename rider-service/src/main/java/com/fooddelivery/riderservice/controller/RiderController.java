package com.fooddelivery.riderservice.controller;

import com.fooddelivery.riderservice.dto.RiderResponse;
import com.fooddelivery.riderservice.service.RiderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/riders")
public class RiderController {

    private final RiderService riderService;

    public RiderController(RiderService riderService) {
        this.riderService = riderService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<RiderResponse> getRider(@PathVariable Long id) {
        return ResponseEntity.ok(riderService.getRider(id));
    }
}
