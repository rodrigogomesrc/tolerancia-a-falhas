package br.ufrn.imd.imd_travel.service;

import br.ufrn.imd.imd_travel.exception.ServiceUnavailableException;
import br.ufrn.imd.imd_travel.model.Flight;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class FlightService {

    @Value("${applications.arlines-hub}")
    private String baseAirlineHubUrl;

    private final RestTemplate restTemplate;
    private final CacheService cacheService;

    public FlightService(@Qualifier("restTemplate5s") RestTemplate restTemplate, CacheService cacheService) {
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

    public Flight getFlightFallback(int flight, String day, Exception e) throws ServiceUnavailableException {
        System.out.println("\nPegando voo do cache no fallback.\n");
        e.printStackTrace();
        String key = "flight_" + flight + "_" + day;
        Flight f = (Flight) cacheService.get(key);
        System.out.println("Voo recuperado do cache: " + f);
        if (f == null) {
            throw new ServiceUnavailableException("Serviço de consulta de voos indisponível. Tente novamente mais tarde.");
        }
        return f;
    }

    public ResponseEntity<String> sellFlightTicketSemResiliencia(int flight, String day, RestTemplate restTemplate) {
        return restTemplate.postForEntity(baseAirlineHubUrl + "/sell?flight=" + flight + "&day=" + day, HttpEntity.EMPTY, String.class);
    }

    @TimeLimiter(name = "airlinesHubSell")
    public CompletableFuture<ResponseEntity<String>> sellFlightTicketComResiliencia(int flight, String day, RestTemplate restTemplate) {
        return CompletableFuture.supplyAsync( () -> restTemplate.postForEntity(baseAirlineHubUrl + "/sell?flight=" + flight + "&day=" + day, HttpEntity.EMPTY, String.class));
    }
}
