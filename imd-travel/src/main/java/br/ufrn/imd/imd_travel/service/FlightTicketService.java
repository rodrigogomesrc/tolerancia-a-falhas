package br.ufrn.imd.imd_travel.service;

import br.ufrn.imd.imd_travel.exception.ServiceUnavailableException;
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

    @Value("${applications.fidelity}")
    private String baseFidelityUrl;


    private final CotacaoService cotacaoService;
    private final FlightService flightService;

    public FlightTicketService(CotacaoService cotacaoService, FlightService flightService) {
        this.cotacaoService = cotacaoService;
        this.flightService = flightService;
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
        Flight f;
        try {
            if (ft) {
                f = flightService.getFlightResiliente(flight, day);
            } else {
                f = flightService.getFlightSemResiliencia(flight, day);
            }
        } catch (ServiceUnavailableException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("Falha ao processar compra de passagem.", e);
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
