package com.ebay.flightbooking.dto;

public class BookingResponse {
    private String id;
    private String flightNumber;
    private String passengerName;
    private int seatsBooked;

    public BookingResponse() {}

    public BookingResponse(String id, String flightNumber, String passengerName, int seatsBooked) {
        this.id = id;
        this.flightNumber = flightNumber;
        this.passengerName = passengerName;
        this.seatsBooked = seatsBooked;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFlightNumber() { return flightNumber; }
    public void setFlightNumber(String flightNumber) { this.flightNumber = flightNumber; }
    public String getPassengerName() { return passengerName; }
    public void setPassengerName(String passengerName) { this.passengerName = passengerName; }
    public int getSeatsBooked() { return seatsBooked; }
    public void setSeatsBooked(int seatsBooked) { this.seatsBooked = seatsBooked; }
}
