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

    @Value("${applications.fidelity}")
    private String baseFidelityUrl;

    public FlightTicketService() {

    }

    public ResponseEntity<String> buyFlight(int flight, String day, long user) {
        RestTemplate restTemplate = new RestTemplate();
        boolean falha = false;

        // Request 1
        ResponseEntity<Flight> responseFlight = restTemplate.getForEntity(baseAirlineHubUrl + "/flight?flight=" + flight + "&day=" + day, Flight.class);

        Flight f = null;
        if (responseFlight.getStatusCode().is2xxSuccessful()) {
            f =  responseFlight.getBody();
        } else if (responseFlight.getStatusCode().is5xxServerError()) {
            falha = true;
        }

        // Request 2
        ResponseEntity<Double> exchangeResponse = restTemplate.getForEntity(baseExchangeUrl + "/convert", Double.class);

        Double cotacaoDolar = null;
        if (exchangeResponse.getStatusCode().is2xxSuccessful()) {
            cotacaoDolar = exchangeResponse.getBody();
        } else if (exchangeResponse.getStatusCode().is5xxServerError()) {
           falha = true;
        }

        // Request 3
        ResponseEntity<String> responseSell = restTemplate.postForEntity(baseAirlineHubUrl + "/sell?flight=" + flight + "&day=" + day, HttpEntity.EMPTY, String.class);

        String transactionId = null;
        if (responseSell.getStatusCode().is2xxSuccessful()) {
            transactionId = responseSell.getBody();
        } else if (responseSell.getStatusCode().is5xxServerError()) {
            falha = true;
        }

        // Request 4
        int bonusValue = Math.round(f.getValue());

        String fidelityUrl = baseFidelityUrl + "/bonus?user=" + user + "&bonus=" + bonusValue;
        ResponseEntity<Void> fidelityResponse = restTemplate.postForEntity(fidelityUrl, HttpEntity.EMPTY, Void.class);
        if (fidelityResponse.getStatusCode().is5xxServerError()) {
            falha = true;
        }

        if (falha) {
            return ResponseEntity.status(500).body("Falha ao processar a compra do voo.");
        }

        return ResponseEntity.ok(transactionId);
    }
}
