package com.ebay.flightbooking.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateFlightRequest {
    @NotBlank
    private String flightNumber;

    @Min(1)
    private int totalSeats;
}
