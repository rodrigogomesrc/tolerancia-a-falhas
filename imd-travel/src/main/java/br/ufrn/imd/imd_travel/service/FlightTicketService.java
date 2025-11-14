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


    private final CotacaoService cotacaoService;

    public FlightTicketService(CotacaoService cotacaoService) {
        this.cotacaoService = cotacaoService;
    }

    public String buyFlight(int flight, String day, long user, boolean ft) {
        RestTemplate restTemplate = new RestTemplate();
        boolean falha = false;

        if(ft){
            System.out.println("Compra com tolerância a falhas");
        } else {
            System.out.println("Compra SEM tolerância a falhas");
        }

        // Request 1 - Consultar voo
        ResponseEntity<Flight> responseFlight = restTemplate.getForEntity(baseAirlineHubUrl + "/flight?flight=" + flight + "&day=" + day, Flight.class);

        Flight f = null;
        if (responseFlight.getStatusCode().is2xxSuccessful()) {
            f =  responseFlight.getBody();
        } else if (responseFlight.getStatusCode().is5xxServerError()) {
            falha = true;
        }

        // Request 2 - Consultar cotação do dólar
        Double cotacaoDolar;
        if (ft){
            System.out.println("Com resiliência");
            cotacaoDolar = cotacaoService.getCotacao();
        } else {
            System.out.println("Sem resiliência");
            cotacaoDolar = cotacaoService.cotacaoRequest();
        }
        if (cotacaoDolar == null) {
            falha = true;
        }

        // Request 3 - Vender passagem
        ResponseEntity<String> responseSell = restTemplate.postForEntity(baseAirlineHubUrl + "/sell?flight=" + flight + "&day=" + day, HttpEntity.EMPTY, String.class);

        String transactionId = null;
        if (responseSell.getStatusCode().is2xxSuccessful()) {
            transactionId = responseSell.getBody();
        } else if (responseSell.getStatusCode().is5xxServerError()) {
            falha = true;
        }

        // Request 4 - Adicionar pontos de fidelidade
        int bonusValue = Math.round(f.getValue());

        String fidelityUrl = baseFidelityUrl + "/bonus?user=" + user + "&bonus=" + bonusValue;
        ResponseEntity<Void> fidelityResponse = restTemplate.postForEntity(fidelityUrl, HttpEntity.EMPTY, Void.class);
        if (fidelityResponse.getStatusCode().is5xxServerError()) {
            falha = true;
        }

        if (falha) {
            throw new RuntimeException("Purchase failed");
        }

        return transactionId;
    }

}
