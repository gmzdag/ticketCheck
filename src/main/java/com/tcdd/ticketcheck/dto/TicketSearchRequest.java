package com.tcdd.ticketcheck.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TicketSearchRequest {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime departureTime;    //
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
    private LocalDateTime departureTimeEnd;
    private String departureStationName;
    private String arrivalStationName;
    private int departureStationId;
    private int arrivalStationId;
    private String seatType;
}
