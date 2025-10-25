package br.ufrn.imd.airlines_hub.controller;

import br.ufrn.imd.airlines_hub.model.Flight;
import br.ufrn.imd.airlines_hub.service.FlightService;
import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class FlightController {

    @Autowired
    private FlightService flightService;

    public FlightController() {}

    @GetMapping("/flight")
    public ResponseEntity<Flight> getFlight(@RequestParam int flight, @RequestParam String day) {

        Flight f = this.flightService.getFlight(flight, day);

        return ResponseEntity.ok(f);
    }

    @PostMapping("/sell")
    public ResponseEntity<String> sellFlight(@RequestParam int flight,  @RequestParam String day) {

        return ResponseEntity.ok(this.flightService.sellFlight(flight, day));
    }
}
