package com.omstu.weatherservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Daily(
        List<String> time,

        // Температура воздуха
        @JsonProperty("temperature_2m_max")
        List<Double> temperatureMax,

        @JsonProperty("temperature_2m_min")
        List<Double> temperatureMin,

        @JsonProperty("temperature_2m_mean")
        List<Double> temperatureMean,

        // Влажность воздуха
        @JsonProperty("relative_humidity_2m_mean")
        List<Double> relativeHumidityMean,

        @JsonProperty("relative_humidity_2m_min")
        List<Double> relativeHumidityMin,

        // Осадки
        @JsonProperty("precipitation_sum")
        List<Double> precipitationSum,

        @JsonProperty("et0_fao_evapotranspiration")
        List<Double> referenceEvapotranspiration,

        // Ветер
        @JsonProperty("wind_speed_10m_max")
        List<Double> windSpeedMax,

        @JsonProperty("wind_gusts_10m_max")
        List<Double> windGustsMax,

        // Солнце
        @JsonProperty("shortwave_radiation_sum")
        List<Double> shortwaveRadiationSum,

        @JsonProperty("sunshine_duration")
        List<Integer> sunshineDuration,

        // Температура почвы
        List<Double> soilTemperature0cmMean,
        List<Double> soilTemperature6cmMean,

        // Влажность почвы
        List<Double> soilMoisture0to1cmMean
) {
}
