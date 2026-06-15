package com.ebay.flightbooking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateBookingRequest {
    @NotBlank
    private String flightNumber;

    @NotBlank
    private String passengerName;

    @Min(1)
    private int seatsBooked;
}
