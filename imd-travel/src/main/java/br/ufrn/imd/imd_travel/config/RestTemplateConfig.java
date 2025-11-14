package br.ufrn.imd.imd_travel.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.net.http.HttpClient;
import java.time.Duration;


@Configuration
public class RestTemplateConfig {

    private static final int TIMEOUT_MS = 2000;

    @Bean
    public RestTemplate restTemplate() {

        var client = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(TIMEOUT_MS))
                .build();

        var requestFactory = new JdkClientHttpRequestFactory(client);
        requestFactory.setReadTimeout(TIMEOUT_MS);

        return new RestTemplate(requestFactory);
    }
}