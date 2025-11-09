package com.omstu.weatherservice.dto;

import java.util.List;

public record Daily(
        List<String> time,
        List<Double> temperature_2m_max,
        List<Double> temperature_2m_min,
        List<Double> precipitation_sum,
        List<Double> et0_fao_evapotranspiration
) {
}
