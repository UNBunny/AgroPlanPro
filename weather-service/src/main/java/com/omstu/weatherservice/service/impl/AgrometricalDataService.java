package com.omstu.weatherservice.service.impl;

import com.omstu.weatherservice.dto.AgrometricalData;
import com.omstu.weatherservice.dto.OpenMeteoResponse;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class AgrometricalDataService {

    public AgrometricalData calculateMetrics(OpenMeteoResponse response) {
        // Допустим, OpenMeteoResponse возвращает списки daily.temperature_2m_max и daily.precipitation_sum
        List<Double> temperatures = response.daily().temperatureMax();
        List<Double> precipitations = response.daily().precipitationSum();

        double sumEffectiveTemp = 0.0;
        double sumPrecipitation = 0.0;
        int heatStressDays = 0;
        double minTemp = Double.MAX_VALUE;

        for (int i = 0; i < temperatures.size(); i++) {
            double temp = temperatures.get(i);
            double rain = (precipitations != null) ? precipitations.get(i) : 0.0;

            // 1. Поиск заморозков
            if (temp < minTemp) {
                minTemp = temp;
            }

            // 2. Дни теплового стресса (если > 30°C)
            if (temp > 30.0) {
                heatStressDays++;
            }

            // 3. Формула Селянинова: только для периодов с T > 10°C
            if (temp > 10.0) {
                sumEffectiveTemp += temp;
                sumPrecipitation += rain;
            }
        }

        // Итоговый расчет ГТК
        // Формула: (Осадки * 10) / Сумма температур
        double gtk = 0.0;
        if (sumEffectiveTemp > 0) {
            gtk = (sumPrecipitation * 10) / sumEffectiveTemp;
        }

        return new AgrometricalData(
                gtk,
                sumPrecipitation,
                sumEffectiveTemp,
                heatStressDays,
                minTemp,
                interpretGtk(gtk)
        );
    }

    private String interpretGtk(double gtk) {
        if (gtk == 0) return "Нет данных (T < 10°C)";
        if (gtk < 0.6) return "Очень сильная засуха";
        if (gtk < 1.0) return "Засушливо";
        if (gtk < 1.3) return "Оптимальное увлажнение";
        if (gtk < 1.6) return "Избыточное увлажнение";
        return "Переувлажнение / Риск гниения";
    }

}
