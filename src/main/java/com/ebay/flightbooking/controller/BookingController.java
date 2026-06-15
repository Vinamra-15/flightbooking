package com.ebay.flightbooking.controller;

import com.ebay.flightbooking.dto.BookingResponse;
import com.ebay.flightbooking.dto.CreateBookingRequest;
import com.ebay.flightbooking.model.Booking;
import com.ebay.flightbooking.service.FlightService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/bookings")
public class BookingController {
    private final FlightService flightService;

    public BookingController(FlightService flightService) {
        this.flightService = flightService;
    }

    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody CreateBookingRequest req, UriComponentsBuilder uriBuilder) {
        Booking b = flightService.book(req);
        var resp = new BookingResponse(b.getId(), b.getFlightNumber(), b.getPassengerName(), b.getSeatsBooked());
        var uri = uriBuilder.path("/bookings/{id}").buildAndExpand(b.getId()).toUri();
        return ResponseEntity.created(uri).body(resp);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelBooking(@PathVariable String id) {
        flightService.cancelBooking(id);
        return ResponseEntity.noContent().build();
    }
}
