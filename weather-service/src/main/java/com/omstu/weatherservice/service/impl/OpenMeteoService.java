package com.omstu.weatherservice.service.impl;

import com.omstu.weatherservice.dto.OpenMeteoResponse;
import com.omstu.weatherservice.dto.WeatherRequestType;
import com.omstu.weatherservice.mapper.OpenMeteoMapper;
import com.omstu.weatherservice.service.ExternalFieldService;
import com.omstu.weatherservice.service.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;


// НЕ ЗАБЫТЬ ДОБАВИТЬ ОГРАНИЧЕНИЕ (01.01.2016 ГОД -> ВСЕ ЧТО МЕНЬШЕ 2016 НЕ КИДАТЬ ЗАПРОС)


@Service
@Slf4j
public class OpenMeteoService implements ExternalFieldService {

    private final WebClient forecastWebClient;
    private final WebClient historicalWebClient;
    private final OpenMeteoMapper openMeteoMapper;

    private static final String HOURLY_PARAMS = String.join(",",
            // Температурные показатели
            "temperature_2m", "dew_point_2m",
            // Влажность и давление
            "relative_humidity_2m", "surface_pressure",
            // Осадки
            "precipitation", "precipitation_probability", "rain", "snowfall",
            // Ветер
            "wind_speed_10m", "wind_direction_10m", "wind_gusts_10m",
            // Солнечная активность
            "sunshine_duration", "shortwave_radiation", "uv_index",
            // Температура почвы
            "soil_temperature_0cm", "soil_temperature_6cm",
            "soil_temperature_18cm", "soil_temperature_54cm",
            // Влажность почвы
            "soil_moisture_0_to_1cm", "soil_moisture_1_to_3cm",
            "soil_moisture_3_to_9cm", "soil_moisture_9_to_27cm",
            "soil_moisture_27_to_81cm"
    );

    private static final String DAILY_PARAMS = String.join(",",
            // Температура
            "temperature_2m_max", "temperature_2m_min", "temperature_2m_mean",
            // Влажность
            "relative_humidity_2m_min", "relative_humidity_2m_mean",
            // Осадки и испарение
            "precipitation_sum", "et0_fao_evapotranspiration",
            // Ветер
            "wind_speed_10m_max", "wind_gusts_10m_max",
            // Солнечная активность
            "shortwave_radiation_sum", "sunshine_duration"
    );

    public OpenMeteoService(WebClient.Builder webClientBuilder, OpenMeteoMapper openMeteoMapper) {
        this.forecastWebClient = webClientBuilder
                .baseUrl("https://api.open-meteo.com/v1")
                .build();

        this.historicalWebClient = webClientBuilder
                .codecs(configurer ->
                        configurer.defaultCodecs().maxInMemorySize(10 * 1024 * 1024)) // 10MB
                .baseUrl("https://historical-forecast-api.open-meteo.com/v1")
                .build();
        this.openMeteoMapper = openMeteoMapper;
    }


    @Override
    public Mono<OpenMeteoResponse> getWeather(
            Double lat, Double lon, WeatherRequestType type,
            Integer days, String startDate, String endDate) {
        if (type == WeatherRequestType.HISTORIC && isLongPeriod(startDate, endDate)) {
            return getHistoricalWeatherByMonths(lat, lon, startDate, endDate);
        }

        return makeSingleRequest(lat, lon, type, days, startDate, endDate);

    }

    private Mono<OpenMeteoResponse> getHistoricalWeatherByMonths(
            Double lat, Double lon, String startDate, String endDate
    ) {
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);

        List<DateUtils.DateRange> monthlyRanges = DateUtils.splitByThreeMonths(start, end);
        log.info("Splitting {} months period into {} monthly requests",
                start.until(end).toTotalMonths(), monthlyRanges.size());

        return Flux.fromIterable(monthlyRanges)
                .concatMap(range ->
                        makeSingleRequest(lat, lon, WeatherRequestType.HISTORIC, null,
                                range.startDate().toString(), range.endDate().toString())
                                .onErrorResume(e -> {
                                    log.warn("Failed to fetch data for range {}: {}", range, e.getMessage());
                                    return Mono.empty();
                                })
                )
                .collectList()
                .map(openMeteoMapper::combineResponses)
                .doOnSuccess(response ->
                        log.info("Successfully combined data from {} monthly requests", monthlyRanges.size()));
    }

    private Mono<OpenMeteoResponse> makeSingleRequest(
            Double lat, Double lon, WeatherRequestType type, Integer days, String startDate, String endDate
    ) {
        WebClient webClient = type == WeatherRequestType.FORECAST ? forecastWebClient : historicalWebClient;

        return webClient.get()
                .uri(uriBuilder -> {
                    uriBuilder
                            .path("/forecast")
                            .queryParam("latitude", lat)
                            .queryParam("longitude", lon)
                            .queryParam("hourly", HOURLY_PARAMS)
                            .queryParam("daily", DAILY_PARAMS)
                            .queryParam("timezone", "auto");

                    if (type == WeatherRequestType.HISTORIC) {
                        uriBuilder.queryParam("start_date", startDate)
                                .queryParam("end_date", endDate);
                    } else {
                        uriBuilder.queryParam("forecast_days", days);
                    }
                    return uriBuilder.build();
                })
                .retrieve()
                .bodyToMono(OpenMeteoResponse.class)
                .doOnSuccess(response ->
                        log.debug("Successfully fetched data for {}-{}", startDate, endDate)
                )
                .doOnError(error ->
                        log.error("Error fetching weather for {}-{}: {}", startDate, endDate, error.getMessage())
                );
    }


    private boolean isLongPeriod(String startDate, String endDate) {
        if (startDate == null || endDate == null) {
            return false;
        }
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        return start.plusMonths(3).isBefore(end);
    }
}