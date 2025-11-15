package br.ufrn.imd.imd_travel.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class RestTemplateConfig {

    @Bean
    @Qualifier("restTemplate2s")
    public RestTemplate restTemplate2s() {
        return createRestTemplate(2000);
    }

    @Bean
    @Qualifier("restTemplate5s")
    public RestTemplate restTemplate5s() {
        return createRestTemplate(5000);
    }

    private RestTemplate createRestTemplate(int timeoutMs) {
        var client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(timeoutMs))
                .build();

        var requestFactory = new JdkClientHttpRequestFactory(client);
        requestFactory.setReadTimeout(timeoutMs);

        return new RestTemplate(requestFactory);
    }
}