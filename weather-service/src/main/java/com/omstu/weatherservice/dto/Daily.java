package com.omstu.weatherservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record Daily(
        List<String> time,

        @JsonProperty("temperature_2m_max")
        List<Double> temperatureMax,

        @JsonProperty("temperature_2m_min")
        List<Double> temperatureMin,

        @JsonProperty("precipitation_sum")
        List<Double> precipitationSum,

        @JsonProperty("et0_fao_evapotranspiration")
        List<Double> referenceEvapotranspiration,

        @JsonProperty("temperature_2m_mean")
        List<Double> temperatureMean,               // Среднесуточная температура

        @JsonProperty("relative_humidity_2m_mean")
        List<Double> relativeHumidityMean,          // Средняя влажность

        @JsonProperty("relative_humidity_2m_min")
        List<Double> relativeHumidityMin,           // Минимальная влажность (для болезней)

        @JsonProperty("wind_speed_10m_max")
        List<Double> windSpeedMax,                  // Максимальная скорость ветра

        @JsonProperty("wind_gusts_10m_max")
        List<Double> windGustsMax,                  // Максимальные порывы ветра

        @JsonProperty("shortwave_radiation_sum")
        List<Double> shortwaveRadiationSum,         // Суммарная радиация

        @JsonProperty("sunshine_duration")
        List<Integer> sunshineDuration,             // Продолжительность солнца (секунды)

        List<Double> soilTemperature0cmMean,        // Средняя темп. почвы поверх.
        List<Double> soilTemperature6cmMean,        // Средняя темп. почвы на 6см
        List<Double> soilMoisture0to1cmMean         // Средняя влажность почвы
) {
}
