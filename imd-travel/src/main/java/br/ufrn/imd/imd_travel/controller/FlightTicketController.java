package br.ufrn.imd.imd_travel.controller;

import br.ufrn.imd.imd_travel.service.FlightTicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FlightTicketController {

    @Autowired
    private FlightTicketService flightTicketService;

    public FlightTicketController() {}

    @PostMapping("/buyTicket")
    public ResponseEntity<String> sellFlight(@RequestParam int flight, @RequestParam String day, @RequestParam long user, @RequestParam boolean ft) {
        // Request 0
        String transactionId = "";
        try {
            transactionId = flightTicketService.buyFlight(flight, day, user, ft);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Erro ao comprar passagem: " + e.getMessage());
        }
        return ResponseEntity.ok(transactionId);
    }
}
