package br.ufrn.imd.imd_travel.service;

import br.ufrn.imd.imd_travel.model.Flight;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FlightTicketService {

    @Value("${applications.arlines-hub}")
    private String baseAirlineHubUrl;

    @Value("${applications.exchange}")
    private String baseExchangeUrl;

    public FlightTicketService() {

    }

    public String buyFlight(int flight, String day) {
        RestTemplate restTemplate = new RestTemplate();

        // Request 1
        ResponseEntity<Flight> responseFlight = restTemplate.getForEntity(baseAirlineHubUrl + "flight?flight=" + flight + "&day=" + day, Flight.class);

        if (responseFlight.getStatusCode().is2xxSuccessful()) {
            Flight f =  responseFlight.getBody();
        }

        // Request 2
        ResponseEntity<Double> exchangeResponse = restTemplate.getForEntity(baseExchangeUrl + "/convert", Double.class);

        if (exchangeResponse.getStatusCode().is2xxSuccessful()) {
            Double cotacaoDolar = exchangeResponse.getBody();
        }

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
