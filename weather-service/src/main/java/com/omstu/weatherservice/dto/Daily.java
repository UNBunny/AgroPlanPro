package com.omstu.weatherservice.dto;

import java.util.List;

public record Daily(
        List<String> time,
        List<Double> temperature_2m_max,
        List<Double> temperature_2m_min,
        List<Double> precipitation_sum,
        List<Double> et0_fao_evapotranspiration,

        List<Double> temperature_2m_mean,           // Среднесуточная температура
        List<Double> relative_humidity_2m_mean,     // Средняя влажность
        List<Double> relative_humidity_2m_min,      // Минимальная влажность (для болезней)
        List<Double> wind_speed_10m_max,           // Максимальная скорость ветра
        List<Double> wind_gusts_10m_max,           // Максимальные порывы ветра
        List<Double> shortwave_radiation_sum,      // Суммарная радиация
        List<Integer> sunshine_duration,           // Продолжительность солнца (секунды)
        List<Double> soil_temperature_0cm_mean,    // Средняя темп. почвы поверх.
        List<Double> soil_temperature_6cm_mean,    // Средняя темп. почвы на 6см
        List<Double> soil_moisture_0_to_1cm_mean   // Средняя влажность почвы
) {
}
