package br.ufrn.imd.airlines_hub.service;

import br.ufrn.imd.airlines_hub.model.Flight;
import jakarta.xml.bind.DatatypeConverter;
import org.springframework.stereotype.Service;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.UUID;

@Service
public class FlightService {

    public FlightService() {}

    public Flight getFlight(int flight, String day) {
        Random random = new Random();

        return new Flight(flight, day,random.nextFloat()*50000 );
    }

    public String sellFlight(int flight, String day) {

        return UUID.randomUUID().toString();
    }
}
