package com.ebay.flightbooking.service;

import com.ebay.flightbooking.dto.CreateBookingRequest;
import com.ebay.flightbooking.dto.CreateFlightRequest;
import com.ebay.flightbooking.model.Booking;
import com.ebay.flightbooking.model.Flight;
import com.ebay.flightbooking.repository.BookingRepository;
import com.ebay.flightbooking.repository.FlightRepository;
import com.ebay.flightbooking.exception.*;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class FlightService {
    private final FlightRepository flightRepository;
    private final BookingRepository bookingRepository;

    public FlightService(FlightRepository flightRepository, BookingRepository bookingRepository) {
        this.flightRepository = flightRepository;
        this.bookingRepository = bookingRepository;
    }

    public Flight createFlight(CreateFlightRequest req) {
        if (flightRepository.exists(req.getFlightNumber())) throw new DuplicateFlightException("Flight already exists");
        Flight f = new Flight(req.getFlightNumber(), req.getTotalSeats());
        return flightRepository.save(f);
    }

    public Booking book(CreateBookingRequest req) {
        if (req.getSeatsBooked() <= 0) throw new IllegalArgumentException("seatsBooked must be > 0");
        Flight f = flightRepository.findByFlightNumber(req.getFlightNumber());
        if (f == null) throw new FlightNotFoundException("Flight not found: " + req.getFlightNumber());

        var lock = f.getLock();
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
        Booking b = bookingRepository.findById(bookingId);
        if (b == null) throw new BookingNotFoundException("Booking not found: " + bookingId);
        Flight f = flightRepository.findByFlightNumber(b.getFlightNumber());
        if (f == null) throw new FlightNotFoundException("Flight not found for booking: " + b.getFlightNumber());

        var lock = f.getLock();
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
