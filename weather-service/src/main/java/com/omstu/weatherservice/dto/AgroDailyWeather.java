package com.omstu.weatherservice.dto;

import java.time.LocalDate;

public record AgroDailyWeather(
        LocalDate date, Double soilMoistureAvg, Double precipitationTotal,
        Double tempMax, Double tempMin, Double solarRadiation, String weatherCondition,
        Double tempAvg, Double tempAmplitude, Double heatStressHours, boolean isFrost,
        Double evapotranspiration, Double waterDeficit, String soilMoistureTrend,
        Double hydroThermalCoefficient, Double diseaseRiskIndex, boolean isFieldWorkable
) {
}
