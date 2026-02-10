package com.omstu.weatherservice.config;

/**
 * Константы параметров для запросов к Open-Meteo API
 */
public final class WeatherParameters {

    private WeatherParameters() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Почасовые параметры для детальных прогнозов
     */
    public static final String HOURLY_PARAMS = String.join(",",
            // Температурные показатели
            "temperature_2m", "dew_point_2m",
            // Влажность и давление
            "relative_humidity_2m", "surface_pressure",
            // Осадки
            "precipitation", "precipitation_probability", "rain", "snowfall",
            // Ветер
            "wind_speed_10m", "wind_direction_10m", "wind_gusts_10m",
            // Солнечная активность
            "sunshine_duration", "shortwave_radiation", "uv_index",
            // Температура почвы
            "soil_temperature_0cm", "soil_temperature_6cm",
            "soil_temperature_18cm", "soil_temperature_54cm",
            // Влажность почвы
            "soil_moisture_0_to_1cm", "soil_moisture_1_to_3cm",
            "soil_moisture_3_to_9cm", "soil_moisture_9_to_27cm",
            "soil_moisture_27_to_81cm"
    );

    /**
     * Дневные агрегированные параметры для исторических данных и ML
     */
    public static final String DAILY_PARAMS = String.join(",",
            // Температура
            "temperature_2m_max", "temperature_2m_min", "temperature_2m_mean",
            // Влажность
            "relative_humidity_2m_min", "relative_humidity_2m_mean",
            // Осадки и испарение
            "precipitation_sum", "et0_fao_evapotranspiration",
            // Ветер
            "wind_speed_10m_max", "wind_gusts_10m_max",
            // Солнечная активность
            "shortwave_radiation_sum", "sunshine_duration"
    );
}

