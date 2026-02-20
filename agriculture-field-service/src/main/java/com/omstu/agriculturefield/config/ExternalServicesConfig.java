package com.omstu.agriculturefield.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ExternalServicesConfig {

    @Value("${services.weather-service.url:http://localhost:8082}")
    private String weatherServiceUrl;

    @Value("${services.ml-service.url:http://localhost:8000}")
    private String mlServiceUrl;

    @Bean("weatherWebClient")
    public WebClient weatherWebClient() {
        return WebClient.builder()
                .baseUrl(weatherServiceUrl)
                .build();
    }

    @Bean("mlWebClient")
    public WebClient mlWebClient() {
        return WebClient.builder()
                .baseUrl(mlServiceUrl)
                .build();
    }
}
