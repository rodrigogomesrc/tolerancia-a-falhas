package br.ufrn.imd.exchange.service;


import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ExchangeService {

    public double getCotacaoDolar() {
        double valor = 1 / (5 + Math.random() * (6 - 5));
        return BigDecimal.valueOf(valor)
                .setScale(4, RoundingMode.HALF_UP)
                .doubleValue();
    }
}
