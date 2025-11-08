package br.ufrn.imd.airlines_hub.controller;

import br.ufrn.imd.airlines_hub.model.Flight;
import br.ufrn.imd.airlines_hub.service.FlightService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Thread.sleep;

@RestController
public class FlightController {

    @Autowired
    private FlightService flightService;

    private final Random random = new Random();

    // Fail(Omission, 0.2, 0s)
    private static final double OMISSION_FAILURE_PROBABILITY = 0.2; // 20%

    // Fail(Time=5s, 0.1, 10s)
    private static final double TIME_FAILURE_PROBABILITY = 0.1; // 10%
    private static final long FAILURE_DURATION_MS = 10000;   // 10 segundos

    private final AtomicBoolean isDelaying = new AtomicBoolean(false);
    private final AtomicLong timeFailureStartTime = new AtomicLong(0);


    public FlightController() {}

    @GetMapping("/flight")
    public ResponseEntity<Flight> getFlight(@RequestParam int flight, @RequestParam String day) throws InterruptedException {

        Flight f = this.flightService.getFlight(flight, day);

        if (random.nextDouble() < OMISSION_FAILURE_PROBABILITY) {
            System.out.println("Omission failure");
            sleep(Long.MAX_VALUE);
        }
        return ResponseEntity.ok(f);
    }

    @PostMapping("/sell")
    public ResponseEntity<String> sellFlight(@RequestParam int flight,  @RequestParam String day) throws InterruptedException {

        if(isDelaying.get() && System.currentTimeMillis() - timeFailureStartTime.get() > FAILURE_DURATION_MS) { // desativa estado de falha
            isDelaying.compareAndSet(true, false);

        } else if(isDelaying.get()) {
            System.out.println("Time failure");
            sleep(5000);

        } else if (random.nextDouble() < TIME_FAILURE_PROBABILITY) { // Decide aleatoriamente se deve entrar em estado de falha de temporização
            if (isDelaying.compareAndSet(false, true)) {
                timeFailureStartTime.set(System.currentTimeMillis());
                System.out.println("Time failure");
                sleep(5000);
            }
        }

        return ResponseEntity.ok(this.flightService.sellFlight(flight, day));
    }
}
