package com.omstu.agriculturefield.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class ExternalServicesConfig {

    @Value("${services.ml-service.url:http://localhost:8000}")
    private String mlServiceUrl;


    @Bean("mlWebClient")
    public WebClient mlWebClient() {
        return WebClient.builder()
                .baseUrl(mlServiceUrl)
                .build();
    }
}
