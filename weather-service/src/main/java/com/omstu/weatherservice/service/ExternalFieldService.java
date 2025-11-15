package com.omstu.weatherservice.service;

import com.omstu.weatherservice.dto.OpenMeteoResponse;
import com.omstu.weatherservice.dto.WeatherRequestType;
import reactor.core.publisher.Mono;

public interface ExternalFieldService {
    Mono<OpenMeteoResponse> getWeather(Double lat, Double lon, WeatherRequestType type, Integer days, String startDate, String endDate);
}
