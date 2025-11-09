package com.omstu.weatherservice.service;

import com.omstu.weatherservice.dto.OpenMeteoResponse;
import reactor.core.publisher.Mono;

public interface ExternalFieldService {

    Mono<OpenMeteoResponse> getDaysWeather(Double lat, Double lon, Integer days);
}
