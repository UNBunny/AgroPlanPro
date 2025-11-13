package com.omstu.weatherservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

// Инфа из апишки для полей
public record Hourly(
        List<String> time,

        // Температура и влажность воздуха
        @JsonProperty("temperature_2m")
        List<Double> temperature,

        @JsonProperty("relative_humidity_2m")
        List<Double> relativeHumidity,

        @JsonProperty("surface_pressure")
        List<Double> surfacePressure,

        @JsonProperty("dew_point_2m")
        List<Double> dewPoint,

        // Осадки
        @JsonProperty("precipitation")
        List<Double> precipitation,

        @JsonProperty("rain")
        List<Double> rain,

        @JsonProperty("snowfall")
        List<Double> snowfall,

        @JsonProperty("precipitation_probability")
        List<Double> precipitationProbability,
        // Ветер
        @JsonProperty("wind_speed_10m")
        List<Double> windSpeed,

        @JsonProperty("wind_gusts_10m")
        List<Double> windGusts,

        @JsonProperty("wind_direction_10m")
        List<Integer> windDirection,

        // Солнце
        @JsonProperty("shortwave_radiation")
        List<Double> shortwaveRadiation,

        @JsonProperty("uv_index")
        List<Double> uvIndex,

        @JsonProperty("sunshine_duration")
        List<Integer> sunshineDuration,

        // Температура почвы (мб лишние удалю)
        @JsonProperty("soil_temperature_0cm")
        List<Double> soilTemperature0cm,

        @JsonProperty("soil_temperature_6cm")
        List<Double> soilTemperature6cm,

        @JsonProperty("soil_temperature_18cm")
        List<Double> soilTemperature18cm,

        @JsonProperty("soil_temperature_54cm")
        List<Double> soilTemperature54cm,

        // Влажность почвы (тоже возможно лишнее есть)
        @JsonProperty("soil_moisture_0_to_1cm")
        List<Double> soilMoisture0To1Cm,

        @JsonProperty("soil_moisture_1_to_3cm")
        List<Double> soilMoisture1To3Cm,

        @JsonProperty("soil_moisture_3_to_9cm")
        List<Double> soilMoisture3To9Cm,

        @JsonProperty("soil_moisture_9_to_27cm")
        List<Double> soilMoisture9To27Cm,

        @JsonProperty("soil_moisture_27_to_81cm")
        List<Double> soilMoisture27To81Cm

) {
}
