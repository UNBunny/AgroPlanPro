package com.omstu.weatherservice.dto;

public record AgrometricalData(
        Double gtk,                // Коэффициент Селянинова
        Double sumPrecipitation,   // Сумма осадков
        Double sumEffectiveTemp,   // Сумма активных температур (>10°C)
        Integer heatStressDays,
        double minTempRecord,
        String stressLevel         // Оценка: "Засуха", "Норма", "Переувлажнение"
) {}