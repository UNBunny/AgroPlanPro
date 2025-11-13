package com.omstu.weatherservice.dto;

import java.time.LocalDate;

public record WeatherRequest(Double latitude, Double longitude, LocalDate date) {
}
