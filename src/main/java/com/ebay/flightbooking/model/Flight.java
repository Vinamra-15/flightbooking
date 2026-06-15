package com.ebay.flightbooking.model;

import java.util.concurrent.locks.ReentrantLock;

public class Flight {
    private String flightNumber;
    private int totalSeats;
    private int bookedSeats;
    private transient ReentrantLock lock = new ReentrantLock();

    public Flight() {}

    public Flight(String flightNumber, int totalSeats) {
        this.flightNumber = flightNumber;
        this.totalSeats = totalSeats;
        this.bookedSeats = 0;
        this.lock = new ReentrantLock();
    }

    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    public int getTotalSeats() { return totalSeats; }
    public void setTotalSeats(int totalSeats) { this.totalSeats = totalSeats; }
    public int getBookedSeats() { return bookedSeats; }
    public void setBookedSeats(int bookedSeats) { this.bookedSeats = bookedSeats; }

    public ReentrantLock getLock() {
        if (lock == null) lock = new ReentrantLock();
        return lock;
    }
}
