package com.tcdd.ticketcheck.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.List;

@Data
public class TicketRequest {

    private String searchType = "DOMESTIC";
    private boolean searchReservation = false;
    List<PassengerTypeCount> passengerTypeCounts;
    List<SearchRoutes> searchRoutes;

    public static record PassengerTypeCount(int id,int count){}

    public static record SearchRoutes(int departureStationId,
                                      String departureStationName,
                                      int arrivalStationId,
                                      String arrivalStationName,
                                      @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
                                      java.time.LocalDateTime departureDate){}

}
