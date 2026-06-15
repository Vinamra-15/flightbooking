package com.ebay.flightbooking.exception;

public class OverbookingException extends RuntimeException {
    public OverbookingException(String msg) { super(msg); }
}
