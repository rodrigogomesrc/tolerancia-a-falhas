package br.ufrn.imd.imd_travel.service;

import br.ufrn.imd.imd_travel.model.Flight;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class FlightService {

    @Value("${applications.arlines-hub}")
    private String baseAirlineHubUrl;

    private final RestTemplate restTemplate;
    private final CacheService cacheService;

    public FlightService(RestTemplate restTemplate, CacheService cacheService) {
        this.restTemplate = restTemplate;
        this.cacheService = cacheService;
    }

    @CircuitBreaker(name = "airlinesHub")
    @Retry(name = "airlinesHub", fallbackMethod = "getFlightFallback")
    public Flight getFlightResiliente(int flight, String day) {
        ResponseEntity<Flight> responseFlight = restTemplate.getForEntity(
                baseAirlineHubUrl + "/flight?flight=" + flight + "&day=" + day, Flight.class);

        if (responseFlight.getStatusCode().is2xxSuccessful()) {
            System.out.println("Voo recuperado com sucesso da API.");
            Flight f = responseFlight.getBody();
            String key = "flight_" + flight + "_" + day;
            cacheService.put(key, f);
            return f;
        } else {
            return null;
        }
    }

    public Flight getFlightSemResiliencia(int flight, String day) {
        ResponseEntity<Flight> responseFlight = restTemplate.getForEntity(
                baseAirlineHubUrl + "/flight?flight=" + flight + "&day=" + day, Flight.class);
        System.out.println("Response Status Voo: " + responseFlight.getStatusCode());

        if (responseFlight.getStatusCode().is2xxSuccessful()) {
            return responseFlight.getBody();
        } else {
            return null;
        }
    }

    public Flight getFlightFallback(int flight, String day, Exception e) {
        System.out.println("\nPegando voo do cache no fallback.\n");
        e.printStackTrace();
        String key = "flight_" + flight + "_" + day;
        Flight f = (Flight) cacheService.get(key);
        System.out.println("Voo recuperado do cache: " + f);
        return f;
        //return (Flight) cacheService.get(key);
    }
}
