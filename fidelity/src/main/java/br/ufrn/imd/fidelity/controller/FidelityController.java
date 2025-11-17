package br.ufrn.imd.fidelity.controller;

import br.ufrn.imd.fidelity.service.FidelityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
public class FidelityController {

    private final FidelityService fidelityService;

    // Fail (Crash, 0.02, _ )
    private static final double FAILURE_PROBABILITY = 0.02; // 2%

    private final Random random = new Random();

    public FidelityController(FidelityService fidelityService) {
        this.fidelityService = fidelityService;
    }

    @PostMapping("/bonus")
    public ResponseEntity addBonus(@RequestParam("user") String userId, @RequestParam("bonus") int bonusValue) {

        // Crash da aplicação
        if (random.nextDouble() < FAILURE_PROBABILITY) {
            System.exit(1);
        }

        fidelityService.addBonus(userId, bonusValue);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
