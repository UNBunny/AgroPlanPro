package com.omstu.agriculturefield.disease.dto;

/**
 * DTO для агрометеорологических данных, получаемых от Weather Service.
 * Зеркалирует com.omstu.weatherservice.dto.AgrometricalData
 */
public record WeatherForecastData(
        Double gtk,
        Double sumPrecipitation,
        Double sumEffectiveTemp,
        Integer heatStressDays,
        Double minTempRecord,
        String stressLevel,
        Double avgTemp,
        Integer extremeHeatDays,
        Integer longestDryPeriod
) {}

