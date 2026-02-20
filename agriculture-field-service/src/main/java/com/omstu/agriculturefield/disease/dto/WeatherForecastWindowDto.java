package com.omstu.agriculturefield.disease.dto;

public record WeatherForecastWindowDto(
        Double tempMean7d,
        Double tempMax7d,
        Double tempMin7d,
        Double humidity7d,
        Double precip7d,
        Double precip14d,
        Double windSpeedMean7d
) {}
