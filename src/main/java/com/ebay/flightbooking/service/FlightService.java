package com.ebay.flightbooking.service;

import com.ebay.flightbooking.dto.CreateBookingRequest;
import com.ebay.flightbooking.dto.CreateFlightRequest;
import com.ebay.flightbooking.model.Booking;
import com.ebay.flightbooking.model.Flight;
import com.ebay.flightbooking.repository.BookingRepository;
import com.ebay.flightbooking.repository.FlightRepository;
import com.ebay.flightbooking.exception.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FlightService {
    private final FlightRepository flightRepository;
    private final BookingRepository bookingRepository;

    public Flight createFlight(CreateFlightRequest req) {
        Objects.requireNonNull(req, "CreateFlightRequest required");
        String flightNumber = req.getFlightNumber();
        if (flightNumber == null || flightNumber.isBlank()) throw new IllegalArgumentException("flightNumber required");
        if (req.getTotalSeats() < 1) throw new IllegalArgumentException("totalSeats must be >= 1");

        Flight flight = new Flight(flightNumber, req.getTotalSeats());
        Flight existing = flightRepository.saveIfAbsent(flight);
        if (existing != null) {
            throw new DuplicateFlightException("Flight already exists: " + flightNumber);
        }
        return flight;
    }

    private Flight getFlightOrThrow(String flightNumber) {
        Flight f = flightRepository.findByFlightNumber(flightNumber);
        if (f == null) throw new FlightNotFoundException("Flight not found: " + flightNumber);
        return f;
    }

    public Booking book(CreateBookingRequest req) {
        Objects.requireNonNull(req, "CreateBookingRequest required");
        if (req.getSeatsBooked() <= 0) throw new IllegalArgumentException("seatsBooked must be > 0");
        if (req.getFlightNumber() == null || req.getFlightNumber().isBlank()) throw new IllegalArgumentException("flightNumber required");
        if (req.getPassengerName() == null || req.getPassengerName().isBlank()) throw new IllegalArgumentException("passengerName required");

        Flight f = getFlightOrThrow(req.getFlightNumber());
        AtomicInteger booked = f.getBookedSeatsAtomic();

        while (true) {
            int current = booked.get();
            int available = f.getTotalSeats() - current;
            if (req.getSeatsBooked() > available) {
                throw new OverbookingException("Not enough seats available");
            }
            int newBooked = current + req.getSeatsBooked();
            if (booked.compareAndSet(current, newBooked)) {
                Booking b = new Booking(UUID.randomUUID().toString(), f.getFlightNumber(), req.getPassengerName(), req.getSeatsBooked());
                bookingRepository.save(b);
                return b;
            }
            // CAS failed, retry
        }
    }

    public void cancelBooking(String bookingId) {
        Objects.requireNonNull(bookingId, "bookingId required");
        Optional<Booking> removedOpt = bookingRepository.delete(bookingId);
        Booking removed = removedOpt.orElseThrow(() -> new BookingNotFoundException("Booking not found or already cancelled: " + bookingId));

        Flight f = getFlightOrThrow(removed.getFlightNumber());
        AtomicInteger booked = f.getBookedSeatsAtomic();
        int seatsToCancel = removed.getSeatsBooked();

        // retry for concurrent modifications
        while (true) {
            int current = booked.get();
            int newBooked = Math.max(0, current - seatsToCancel);
            if (booked.compareAndSet(current, newBooked)) {
                break;
            }
        }
    }
}
