package com.omstu.weatherservice.service.strategy;

import com.omstu.weatherservice.config.WeatherParameters;
import com.omstu.weatherservice.dto.OpenMeteoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Стратегия для получения прогноза погоды
 */
@Slf4j
public class ForecastRequestStrategy implements WeatherRequestStrategy {

    private final Integer forecastDays;

    public ForecastRequestStrategy(Integer forecastDays) {
        this.forecastDays = forecastDays;
    }

    @Override
    public Mono<OpenMeteoResponse> execute(WebClient webClient, Double lat, Double lon) {
        log.debug("Requesting {} days forecast for coordinates: lat={}, lon={}", forecastDays, lat, lon);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/forecast")
                        .queryParam("latitude", lat)
                        .queryParam("longitude", lon)
                        .queryParam("hourly", WeatherParameters.HOURLY_PARAMS)
                        .queryParam("daily", WeatherParameters.DAILY_PARAMS)
                        .queryParam("forecast_days", forecastDays)
                        .queryParam("timezone", "auto")
                        .build())
                .retrieve()
                .bodyToMono(OpenMeteoResponse.class)
                .doOnSuccess(response ->
                        log.info("Successfully received {} days forecast for coordinates: lat={}, lon={}",
                                forecastDays, lat, lon))
                .doOnError(error ->
                        log.error("Failed to fetch forecast for coordinates lat={}, lon={}: {}",
                                lat, lon, error.getMessage()));
    }

    @Override
    public String getType() {
        return "FORECAST";
    }
}

