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
import java.util.concurrent.locks.ReentrantLock;

@Service
@RequiredArgsConstructor
public class FlightService {
    private final FlightRepository flightRepository;
    private final BookingRepository bookingRepository;

    public Flight createFlight(CreateFlightRequest req) {
        Objects.requireNonNull(req, "CreateFlightRequest required");
        String flightNumber = req.getFlightNumber();
        if (flightRepository.exists(flightNumber)) {
            throw new DuplicateFlightException("Flight already exists: " + flightNumber);
        }
        Flight f = new Flight(flightNumber, req.getTotalSeats());
        return flightRepository.save(f);
    }

    private Flight getFlightOrThrow(String flightNumber) {
        Flight f = flightRepository.findByFlightNumber(flightNumber);
        if (f == null) throw new FlightNotFoundException("Flight not found: " + flightNumber);
        return f;
    }

    public Booking book(CreateBookingRequest req) {
        Objects.requireNonNull(req, "CreateBookingRequest required");
        if (req.getSeatsBooked() <= 0) throw new IllegalArgumentException("seatsBooked must be > 0");

        Flight f = getFlightOrThrow(req.getFlightNumber());
        ReentrantLock lock = f.getLock();
        lock.lock();
        try {
            int available = f.getTotalSeats() - f.getBookedSeats();
            if (req.getSeatsBooked() > available) {
                throw new OverbookingException("Not enough seats available");
            }
            f.setBookedSeats(f.getBookedSeats() + req.getSeatsBooked());
            Booking b = new Booking(UUID.randomUUID().toString(), f.getFlightNumber(), req.getPassengerName(), req.getSeatsBooked());
            bookingRepository.save(b);
            return b;
        } finally {
            lock.unlock();
        }
    }

    public void cancelBooking(String bookingId) {
        Objects.requireNonNull(bookingId, "bookingId required");
        Booking b = bookingRepository.findById(bookingId);
        if (b == null) throw new BookingNotFoundException("Booking not found: " + bookingId);
        Flight f = getFlightOrThrow(b.getFlightNumber());

        ReentrantLock lock = f.getLock();
        lock.lock();
        try {
            int newBooked = f.getBookedSeats() - b.getSeatsBooked();
            f.setBookedSeats(Math.max(0, newBooked));
            bookingRepository.delete(bookingId);
        } finally {
            lock.unlock();
        }
    }
}
