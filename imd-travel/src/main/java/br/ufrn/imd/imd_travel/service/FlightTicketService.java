package br.ufrn.imd.imd_travel.service;

import br.ufrn.imd.imd_travel.model.Flight;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FlightTicketService {

    public FlightTicketService() {}

    public String buyFlight(int flight, String day) {
        RestTemplate restTemplate = new RestTemplate();

        String baseAirlineHubUrl = "http://localhost:8081/";

        // Request 1
        ResponseEntity<Flight> responseFlight = restTemplate.getForEntity(baseAirlineHubUrl + "flight?flight=" + flight + "&day=" + day, Flight.class);

        if (responseFlight.getStatusCode().is2xxSuccessful()) {
            Flight f =  responseFlight.getBody();
        }

        // TODO: Request 2

        // Request 3
        ResponseEntity<String> responseSell = restTemplate.postForEntity(baseAirlineHubUrl + "sell?flight=" + flight + "&day=" + day, HttpEntity.EMPTY, String.class);

        String transactionId = null;
        if (responseSell.getStatusCode().is2xxSuccessful()) {
            transactionId = responseSell.getBody();
        }

        // TODO: Request 4

        return transactionId;
    }
}
