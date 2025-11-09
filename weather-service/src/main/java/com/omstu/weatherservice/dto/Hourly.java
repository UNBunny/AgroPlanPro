package com.omstu.weatherservice.dto;

import java.util.List;

// Инфа из апишки для полей
public record Hourly(
        List<String> time,

        // Температура и влажность воздуха
        List<Double> temperature_2m, List<Double> relative_humidity_2m, List<Double> surface_pressure,

        // Осадки
        List<Double> precipitation, List<Double> rain, List<Double> snowfall,

        // Ветер
        List<Double> wind_speed_10m,

        // Солнце
        List<Double> shortwave_radiation,
        List<Double> uv_index,

        // Температура почвы (мб лишние удалю)
        List<Double> soil_temperature_0cm,
        List<Double> soil_temperature_6cm,
        List<Double> soil_temperature_18cm,
        List<Double> soil_temperature_54cm,

        // Влажность почвы (тоже возможно лишнее есть)
        List<Double> soil_moisture_0_to_1cm,
        List<Double> soil_moisture_1_to_3cm,
        List<Double> soil_moisture_3_to_9cm,
        List<Double> soil_moisture_9_to_27cm,
        List<Double> soil_moisture_27_to_81cm

) {
}
