package com.tcdd.ticketcheck.service;

import com.tcdd.ticketcheck.dto.FilteredTrainDetails;
import com.tcdd.ticketcheck.dto.TicketRequest;
import com.tcdd.ticketcheck.dto.TicketResponse;
import com.tcdd.ticketcheck.dto.TicketSearchRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketCheckService {

    private final RestClient restClient;
    private final EmailService emailService;

    @Value("${tcdd.api.url}")
    private String tcddApiUrl;

    @Value("${tcdd.api.auth.token}")
    private String tcddAuthToken;

    @Value("${tcdd.api.unit-id}")
    private String tcddUnitId;

    @Value("${app.notification.email.to}")
    private String notificationEmailTo;


    public List<FilteredTrainDetails> getTrain(TicketSearchRequest request) {

        TicketRequest ticketRequest = prepareTicketRequest(request);
        TicketResponse response = fetchTrainData(ticketRequest);

        List<FilteredTrainDetails> availableTickets = filterAvailableTrains(response, request);

        if (!availableTickets.isEmpty()) {
            sendEmailForAvailableSeats(notificationEmailTo, availableTickets);
        }
        return availableTickets;
    }

    // Kullanıcıdan gelen request verisini alır
    private TicketRequest prepareTicketRequest(TicketSearchRequest request) {

        var ticketRequest = new TicketRequest();

        ticketRequest.setSearchRoutes(List.of(new TicketRequest.SearchRoutes(
                request.getDepartureStationId(),
                request.getDepartureStationName(),
                request.getArrivalStationId(),
                request.getArrivalStationName(),
                request.getDepartureTime()
        )));
        ticketRequest.setPassengerTypeCounts(List.of(new TicketRequest.PassengerTypeCount(0, 1)));
        ticketRequest.setSearchReservation(false);
        ticketRequest.setSearchType("DOMESTIC");
        return ticketRequest;
    }

    //
    private TicketResponse fetchTrainData(TicketRequest ticketRequest) {

        return restClient.post().uri(tcddApiUrl).body(ticketRequest).
                header("Authorization", tcddAuthToken).
                header("Unit-Id", tcddUnitId).
                retrieve().body(TicketResponse.class);
    }

    private List<FilteredTrainDetails> filterAvailableTrains(TicketResponse response, TicketSearchRequest request) {
        List<FilteredTrainDetails> availableTickets = new ArrayList<>();
        if (response == null || response.getTrainLegs() == null) return availableTickets;

        for (var trainLeg : response.getTrainLegs()) {
            for (var availability : trainLeg.trainAvailabilities()) {
                for (var train : availability.trains()) {
                    var departureTime = train.trainSegments().get(0).departureTime();
                    var arrivalTime = train.trainSegments().get(0).arrivalTime();
                    if (departureTime.isBefore(request.getDepartureTime()) || departureTime.isAfter(request.getDepartureTimeEnd()))
                        continue;

                    for (var fareInfo : train.availableFareInfo()) {
                        for (var cabinClass : fareInfo.cabinClasses()) {
                            if (cabinClass.cabinClass().name().equalsIgnoreCase(request.getSeatType()) &&
                                    cabinClass.availabilityCount() > 0) {

                                FilteredTrainDetails filteredTrainDetails = new FilteredTrainDetails(
                                        train.name(),
                                        train.number(),
                                        request.getDepartureStationName(),
                                        request.getArrivalStationName(),
                                        departureTime,
                                        arrivalTime,
                                        request.getSeatType(),
                                        cabinClass.availabilityCount()
                                );
                                availableTickets.add(filteredTrainDetails);
                            }
                        }
                    }
                }
            }
        }
        return availableTickets;
    }

    private void sendEmailForAvailableSeats(String to, List<FilteredTrainDetails> tickets) {
        StringBuilder mailContent = new StringBuilder();
        mailContent.append("Aşağıdaki trenlerde uygun koltuk bulunmaktadır:\n\n");

        for (FilteredTrainDetails filteredTrainDetails : tickets) {
            mailContent.append("Tren Adı: ").append(filteredTrainDetails.trainName()).append("\n")
                    .append("Kalkış: ").append(filteredTrainDetails.departureStationName())
                    .append(" - ").append("Varış: ").append(filteredTrainDetails.arrivalStationName()).append("\n")
                    .append("Saat: ").append(filteredTrainDetails.departureTime()).append("\n")
                    .append("Koltuk Sınıfı: ").append(filteredTrainDetails.seatType()).append("\n")
                    .append("Boş Koltuk Sayısı: ").append(filteredTrainDetails.availableSeats()).append("\n\n");
        }

        emailService.sendEmail(to, "Uygun Tren Biletleri Mevcut", mailContent.toString());
    }


}