package com.ebay.flightbooking.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.locks.ReentrantLock;

@Data
@NoArgsConstructor
public class Flight {
    private String flightNumber;
    private int totalSeats;
    private int bookedSeats;
    private transient ReentrantLock lock = new ReentrantLock();

    public Flight(String flightNumber, int totalSeats) {
        this.flightNumber = flightNumber;
        this.totalSeats = totalSeats;
        this.bookedSeats = 0;
        this.lock = new ReentrantLock();
    }

    public ReentrantLock getLock() {
        if (lock == null) lock = new ReentrantLock();
        return lock;
    }
}
