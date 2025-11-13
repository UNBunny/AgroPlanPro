package com.omstu.weatherservice.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record WeatherResponse(Double temperature, Double humidity, Double precipitation, Double pressure, Double windSpeed, Double soil_moisture,
                              String description, LocalDateTime timestamp, String location
) {
}
