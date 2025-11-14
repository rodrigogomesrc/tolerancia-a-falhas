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
        System.out.println("Iniciando cotacao request");
        ResponseEntity<Double> exchangeResponse = restTemplate.getForEntity(baseExchangeUrl + "/convert", Double.class);
        Double cotacaoDolar = null;
        System.out.println("Response Status Cotação: " + exchangeResponse.getStatusCode());
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
            cacheCotacao.add(cotacaoDolar);
            return cotacaoDolar;
        }
        return null;
    }


    public Double getCotacaoFallback(Exception e){
        System.out.println("Pegando cotação do cache no fallback.");
        e.printStackTrace();
        return cacheCotacao.getAverage();
    }

}
