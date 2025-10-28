package br.ufrn.imd.fidelity.controller;

import br.ufrn.imd.fidelity.service.FidelityService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FidelityController {

    private final FidelityService fidelityService;

    public FidelityController(FidelityService fidelityService) {
        this.fidelityService = fidelityService;
    }

    @PostMapping("/bonus")
    public ResponseEntity addBonus(@RequestParam("user") String userId, @RequestParam("bonus") int bonusValue) {

        fidelityService.addBonus(userId, bonusValue);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
