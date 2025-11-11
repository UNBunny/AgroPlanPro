package com.omstu.weatherservice.service.impl;

import com.omstu.weatherservice.dto.OpenMeteoResponse;
import com.omstu.weatherservice.dto.WeatherRequestType;
import com.omstu.weatherservice.service.ExternalFieldService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class OpenMeteoService implements ExternalFieldService {

    private final WebClient forecastWebClient;
    private final WebClient historicalWebClient;


    public OpenMeteoService(WebClient.Builder webClientBuilder, WebClient.Builder historicalWebClient) {
        this.forecastWebClient = webClientBuilder
                .baseUrl("https://api.open-meteo.com/v1")
                .build();

        this.historicalWebClient = webClientBuilder
                .baseUrl("https://historical-forecast-api.open-meteo.com/v1")
                .build();
    }


    @Override
    public Mono<OpenMeteoResponse> getWeather(Double lat, Double lon, WeatherRequestType type, Integer days, String startDate, String endDate) {
        WebClient webClient = type == WeatherRequestType.FORECAST ? forecastWebClient : historicalWebClient;
        return webClient.get()
                .uri(uriBuilder -> {
                            UriBuilder builder = uriBuilder
                                    .path("/forecast")
                                    .queryParam("latitude", lat)
                                    .queryParam("longitude", lon)
                                    .queryParam("hourly", "temperature_2m,relative_humidity_2m,surface_pressure," +
                                            "precipitation,rain,snowfall,wind_speed_10m,shortwave_radiation,uv_index," +
                                            "soil_temperature_0cm,soil_temperature_6cm,soil_temperature_18cm,soil_temperature_54cm," +
                                            "soil_moisture_0_to_1cm,soil_moisture_1_to_3cm,soil_moisture_3_to_9cm," +
                                            "soil_moisture_9_to_27cm,soil_moisture_27_to_81cm")
                                    .queryParam("daily", "temperature_2m_max,temperature_2m_min,precipitation_sum,et0_fao_evapotranspiration")
                                    .queryParam("timezone", "auto");

                            if (type == WeatherRequestType.HISTORIC) {
                                uriBuilder.queryParam("start_date", startDate);
                                uriBuilder.queryParam("end_date", endDate);
                            } else {
                                uriBuilder.queryParam("forecast_days", days);
                            }
                            return builder.build();
                        }
                )
                .retrieve()
                .bodyToMono(OpenMeteoResponse.class)
                .doOnError(error -> log.error("Error fetching weather from Open-Meteo", error));
    }
}
