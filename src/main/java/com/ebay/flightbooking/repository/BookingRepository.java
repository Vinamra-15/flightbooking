package com.ebay.flightbooking.repository;

import com.ebay.flightbooking.model.Booking;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class BookingRepository {
    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();

    public Booking save(Booking b) { bookings.put(b.getId(), b); return b; }
    public Booking findById(String id) { return bookings.get(id); }
    public Booking delete(String id) { return bookings.remove(id); }
}
