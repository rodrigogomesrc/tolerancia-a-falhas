package br.ufrn.imd.imd_travel.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CotacaoService {

    @Value("${applications.exchange}")
    private String baseExchangeUrl;

    private final BufferCacheService cacheCotacao;
    private final RestTemplate restTemplate;

    public CotacaoService(@Qualifier("cotacao") BufferCacheService cacheCotacao, RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
        this.cacheCotacao = cacheCotacao;
    }

    public Double cotacaoRequest(){
        ResponseEntity<Double> exchangeResponse = restTemplate.getForEntity(baseExchangeUrl + "/convert", Double.class);
        Double cotacaoDolar = null;
        if (exchangeResponse.getStatusCode().is2xxSuccessful()) {
            cotacaoDolar = exchangeResponse.getBody();
        }
        return cotacaoDolar;
    }

    @CircuitBreaker(name = "airlinesHub", fallbackMethod = "getCotacaoFallback")
    @Retry(name = "airlinesHub")
    public Double getCotacao(){
        Double cotacaoDolar = cotacaoRequest();
        if (cotacaoDolar != null) {
            System.out.println("Pegando cotação do serviço");
            cacheCotacao.add(cotacaoDolar);
            return cotacaoDolar;
        } else {
            throw new RuntimeException("Falha em obter a cotação do dólar");
        }
    }


    public Double getCotacaoFallback(){
        System.out.println("Pegando cotação do cache no fallback");
        return cacheCotacao.getAverage();
    }

}
