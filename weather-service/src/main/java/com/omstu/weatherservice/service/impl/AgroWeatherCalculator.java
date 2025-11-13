package com.omstu.weatherservice.service.impl;

import org.springframework.stereotype.Service;

@Service
public class AgroWeatherCalculator {

    private Double calculateTempAvg(Double tempMax, Double tempMin) {
        return (tempMax + tempMin) / 2;
    }

    private Double calculateTempAmplitude(Double tempMax, Double tempMin) {
        return (tempMax - tempMin);
    }

}
