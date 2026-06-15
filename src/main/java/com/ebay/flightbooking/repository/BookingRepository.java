package com.ebay.flightbooking.repository;

import com.ebay.flightbooking.model.Booking;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

@Repository
public class BookingRepository {
    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();

    public Booking save(Booking b) { bookings.put(b.getId(), b); return b; }
    public Optional<Booking> findById(String id) { return Optional.ofNullable(bookings.get(id)); }
    public Optional<Booking> delete(String id) { return Optional.ofNullable(bookings.remove(id)); }
}
