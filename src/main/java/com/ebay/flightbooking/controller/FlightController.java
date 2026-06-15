package com.ebay.flightbooking.controller;

import com.ebay.flightbooking.dto.CreateFlightRequest;
import com.ebay.flightbooking.model.Flight;
import com.ebay.flightbooking.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/flights")
public class FlightController {
    private final FlightService flightService;

    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping
    public ResponseEntity<Void> createFlight(@Valid @RequestBody CreateFlightRequest req) {
        Flight f = flightService.createFlight(req);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{flightNumber}")
                .buildAndExpand(f.getFlightNumber())
                .toUri();
        return ResponseEntity.created(uri).build();
    }
}
