package br.ufrn.imd.fidelity.controller;

import br.ufrn.imd.fidelity.service.FidelityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
public class FidelityController {

    private final FidelityService fidelityService;

    // Fail (Crash, 0.02, _ )
    private static final double FAILURE_PROBABILITY = 0.02; // 2%

    private final Random random = new Random();
    private final AtomicBoolean isCrashed  = new AtomicBoolean(false);

    public FidelityController(FidelityService fidelityService) {
        this.fidelityService = fidelityService;
    }

    @PostMapping("/bonus")
    public ResponseEntity addBonus(@RequestParam("user") String userId, @RequestParam("bonus") int bonusValue) {

        // Se o sistema "crashou", ele não responde até reiniciar
        if (isCrashed.get()) {
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
        }

        // Decide aleatoriamente se deve "crashar"
        if (random.nextDouble() < FAILURE_PROBABILITY) {
            if (isCrashed.compareAndSet(false, true)) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).build();
            }
        }

        fidelityService.addBonus(userId, bonusValue);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
