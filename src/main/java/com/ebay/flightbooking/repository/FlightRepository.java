package com.ebay.flightbooking.repository;

import com.ebay.flightbooking.model.Flight;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import org.springframework.stereotype.Repository;

@Repository
public class FlightRepository {
    private final Map<String, Flight> flights = new ConcurrentHashMap<>();

    public Flight save(Flight f) { flights.put(f.getFlightNumber(), f); return f; }
    public Flight findByFlightNumber(String flightNumber) { return flights.get(flightNumber); }
    public boolean exists(String flightNumber) { return flights.containsKey(flightNumber); }
}
