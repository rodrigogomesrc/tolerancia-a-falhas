package br.ufrn.imd.imd_travel.config;

import br.ufrn.imd.imd_travel.service.BufferCacheService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CacheConfig {

    @Bean(name = "cotacao")
    public BufferCacheService cacheTemperatura() {
        return new BufferCacheService(10);
    }

}
