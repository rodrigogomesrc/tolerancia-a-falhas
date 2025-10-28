package br.ufrn.imd.fidelity.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class FidelityService {
    private final Map<String, Integer> userBonus = new HashMap<>();

    public void addBonus(String userId, int bonusValue) {
        int currentBonus = userBonus.getOrDefault(userId, 0);
        userBonus.put(userId, currentBonus + bonusValue);
    }
}
