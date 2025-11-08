package com.omstu.weatherservice.dto;

// Инфа из апишки для полей
public record Current(
        String time,

        // Температура и влажность воздуха
        Double temperature_2m, Double relative_humidity_2m, Double surface_pressure,

        // Осадки
        Double precipitation, Double rain, Double snowfall,

        // Ветер
        Double wind_speed_10m,
        Double soil_moisture_9_27cm,

        // Солнце
        Double shortwave_radiation,
        Double uv_index,

        // Температура почвы (мб лишние удалю)
        Double soil_temperature_0cm,
        Double soil_temperature_6cm,
        Double soil_temperature_18cm,
        Double soil_temperature_54cm,

        // Влажность почвы (тоже возможно лишнее есть)
        Double soil_moisture_0_to_1cm,
        Double soil_moisture_1_to_3cm,
        Double soil_moisture_3_to_9cm,
        Double soil_moisture_9_to_27cm,
        Double soil_moisture_27_to_81cm

) {
}
