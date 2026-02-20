package com.omstu.agriculturefield.rotation.dto;

public record WeatherForecastDto(
        Double tempMean7d,
        Double tempMax7d,
        Double humidity7d,
        Double precip7d,
        Double precip14d,
        Double windSpeedMean
) {}
