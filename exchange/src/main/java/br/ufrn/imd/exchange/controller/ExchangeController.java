package br.ufrn.imd.exchange.controller;


import br.ufrn.imd.exchange.service.ExchangeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ExchangeController {

    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }

    @GetMapping("/convert")
    public ResponseEntity<Double> convert(){
        Double cotacao = exchangeService.getCotacaoDolar();
        return new ResponseEntity<>(cotacao, HttpStatus.OK);
    }


}
