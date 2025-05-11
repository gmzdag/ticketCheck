package com.tcdd.ticketcheck.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public record FilteredTrainDetails(
        String trainName,
        String trainNumber,
        String departureStationName,
        String arrivalStationName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime departureTime,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
        LocalDateTime arrivalTime,
        String seatType,
        int availableSeats
) {
}