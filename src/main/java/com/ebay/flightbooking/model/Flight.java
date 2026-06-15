package com.ebay.flightbooking.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.atomic.AtomicInteger;

@Data
@NoArgsConstructor
public class Flight {
    private String flightNumber;
    private int totalSeats;
    private transient AtomicInteger bookedSeatsAtomic = new AtomicInteger(0);

    public Flight(String flightNumber, int totalSeats) {
        this.flightNumber = flightNumber;
        this.totalSeats = totalSeats;
        this.bookedSeatsAtomic = new AtomicInteger(0);
    }

    public int getBookedSeats() {
        return bookedSeatsAtomic.get();
    }

    public void setBookedSeats(int seats) {
        bookedSeatsAtomic.set(seats);
    }

    public AtomicInteger getBookedSeatsAtomic() {
        if (bookedSeatsAtomic == null) bookedSeatsAtomic = new AtomicInteger(0);
        return bookedSeatsAtomic;
    }
}
