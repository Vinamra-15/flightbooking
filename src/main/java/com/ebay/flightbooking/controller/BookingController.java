package com.ebay.flightbooking.controller;

import com.ebay.flightbooking.dto.BookingResponse;
import com.ebay.flightbooking.dto.CreateBookingRequest;
import com.ebay.flightbooking.model.Booking;
import com.ebay.flightbooking.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import lombok.RequiredArgsConstructor;

import java.net.URI;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final FlightService flightService;

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest req) {
        Booking b = flightService.book(req);
        BookingResponse resp = new BookingResponse(b.getId(), b.getFlightNumber(), b.getPassengerName(), b.getSeatsBooked());
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(b.getId())
                .toUri();
        return ResponseEntity.created(uri).body(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable String id) {
        flightService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }
}
