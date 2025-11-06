package br.ufrn.imd.exchange.controller;

import br.ufrn.imd.exchange.service.ExchangeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@RestController
public class ExchangeController {

    private final ExchangeService exchangeService;

    // Fail (Error, 0.1, 5s)
    private static final double FAILURE_PROBABILITY = 0.10; // 10%
    private static final long FAILURE_DURATION_MS = 5000;   // 5 segundos

    private final Random random = new Random();

    private final AtomicBoolean isFailing = new AtomicBoolean(false);
    private final AtomicLong failureStartTime = new AtomicLong(0);

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @GetMapping("/convert")
    public ResponseEntity<Double> convert() {
        long currentTime = System.currentTimeMillis();

        //  Verifica se a falha ainda está ativa (dentro dos 5s)
        if (isFailing.get()) {
            if (currentTime - failureStartTime.get() < FAILURE_DURATION_MS) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            } else {
                isFailing.compareAndSet(true, false);
            }
        }

        // Decide aleatoriamente se deve iniciar uma falha
        if (random.nextDouble() < FAILURE_PROBABILITY) {
            if (isFailing.compareAndSet(false, true)) {
                failureStartTime.set(currentTime);
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        Double cotacao = exchangeService.getCotacaoDolar();
        return new ResponseEntity<>(cotacao, HttpStatus.OK);
    }
}