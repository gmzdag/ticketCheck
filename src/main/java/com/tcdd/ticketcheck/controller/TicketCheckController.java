package com.tcdd.ticketcheck.controller;

import com.tcdd.ticketcheck.dto.FilteredTrainDetails;
import com.tcdd.ticketcheck.dto.TicketSearchRequest;
import com.tcdd.ticketcheck.service.TicketCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("ticket")
@RequiredArgsConstructor
public class TicketCheckController {

    private final TicketCheckService ticketCheckService;

    @PostMapping("/check")
    public ResponseEntity<List<FilteredTrainDetails>> searchTickets(@RequestBody TicketSearchRequest request) {
        List<FilteredTrainDetails> availableTickets = ticketCheckService.getTrain(request);
        return ResponseEntity.ok(availableTickets);
    }



}

