package br.ufrn.imd.imd_travel.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CacheService {

   private final Map<String, Object> cache = new ConcurrentHashMap<>();

    public void put(String key, Object value) {
         cache.put(key, value);
    }

    public Object get(String key) {
         return cache.get(key);
    }
}
