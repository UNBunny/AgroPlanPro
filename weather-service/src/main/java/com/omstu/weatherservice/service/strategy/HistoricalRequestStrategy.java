package com.omstu.weatherservice.service.strategy;

import com.omstu.weatherservice.config.WeatherParameters;
import com.omstu.weatherservice.dto.OpenMeteoResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Стратегия для получения исторических данных (только дневные агрегаты для ML)
 */
@Slf4j
public class HistoricalRequestStrategy implements WeatherRequestStrategy {

    private final String startDate;
    private final String endDate;

    public HistoricalRequestStrategy(String startDate, String endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    @Override
    public Mono<OpenMeteoResponse> execute(WebClient webClient, Double lat, Double lon) {
        log.debug("Requesting historical data for period {} to {} at coordinates: lat={}, lon={}",
                startDate, endDate, lat, lon);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/archive")
                        .queryParam("latitude", lat)
                        .queryParam("longitude", lon)
                        .queryParam("daily", WeatherParameters.DAILY_PARAMS)
                        .queryParam("start_date", startDate)
                        .queryParam("end_date", endDate)
                        .queryParam("timezone", "auto")
                        .build())
                .retrieve()
                .bodyToMono(OpenMeteoResponse.class)
                .doOnSuccess(response ->
                        log.info("Successfully received historical data for period {} to {} at lat={}, lon={}",
                                startDate, endDate, lat, lon))
                .doOnError(error ->
                        log.error("Failed to fetch historical data for period {} to {} at lat={}, lon={}: {}",
                                startDate, endDate, lat, lon, error.getMessage()));
    }

    @Override
    public String getType() {
        return "HISTORICAL";
    }
}

