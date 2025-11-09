package com.omstu.weatherservice.service;

import com.omstu.weatherservice.dto.OpenMeteoResponse;
import jakarta.ws.rs.core.UriBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class OpenMeteoService implements ExternalFieldService {

    private final WebClient webClient;

    public OpenMeteoService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
                .baseUrl("https://api.open-meteo.com/v1")
                .build();
    }


    @Override
    public Mono<OpenMeteoResponse> getDaysWeather(Double lat, Double lon, Integer days) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/forecast")
                        .queryParam("latitude", lat)
                        .queryParam("longitude", lon)
                        .queryParam("hourly", "temperature_2m,relative_humidity_2m,surface_pressure," +
                                "precipitation,rain,snowfall,wind_speed_10m,shortwave_radiation,uv_index," +
                                "soil_temperature_0cm,soil_temperature_6cm,soil_temperature_18cm,soil_temperature_54cm," +
                                "soil_moisture_0_to_1cm,soil_moisture_1_to_3cm,soil_moisture_3_to_9cm," +
                                "soil_moisture_9_to_27cm,soil_moisture_27_to_81cm")
                        .queryParam("daily", "temperature_2m_max,temperature_2m_min,precipitation_sum,et0_fao_evapotranspiration")
                        .queryParam("forecast_days", days)
                        .queryParam("timezone", "auto")
                        .build()
                )
                .retrieve()
                .bodyToMono(OpenMeteoResponse.class)
                .doOnError(error -> log.error("Error fetching weather from Open-Meteo", error));
    }

//    private WeatherResponse mapToWeatherResponse(OpenMeteoResponse openMeteoResponse) {
//        return WeatherResponse.builder()
//                .temperature(openMeteoResponse.current().temperature_2m())
//                .humidity(openMeteoResponse.current().relative_humidity_2m())
//                .precipitation(openMeteoResponse.current().precipitation())
//                .pressure(openMeteoResponse.current().surface_pressure())
//                .windSpeed(openMeteoResponse.current().wind_speed_10m())
//                .soil_moisture(openMeteoResponse.current().soil_moisture_9_27cm())
//                .build();
//    }

}
