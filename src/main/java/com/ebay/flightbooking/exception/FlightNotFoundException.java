package com.ebay.flightbooking.exception;

public class FlightNotFoundException extends RuntimeException {
    public FlightNotFoundException(String msg) { super(msg); }
}
