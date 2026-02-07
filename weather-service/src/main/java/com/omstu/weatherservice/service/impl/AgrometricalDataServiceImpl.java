package com.omstu.weatherservice.service.impl;

import com.omstu.weatherservice.dto.AgrometricalData;
import com.omstu.weatherservice.dto.OpenMeteoResponse;
import com.omstu.weatherservice.dto.WeatherRequestType;
import com.omstu.weatherservice.service.AgroMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Реализация сервиса для расчета агрометеорологических показателей
 * Рассчитывает ГТК (гидротермический коэффициент Селянинова), тепловой стресс и другие метрики
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AgrometricalDataServiceImpl implements AgroMetricsService {

    private final OpenMeteoService openMeteoService;

    // Константы для расчетов
    private static final double ACTIVE_TEMP_THRESHOLD = 10.0;
    private static final double HEAT_STRESS_THRESHOLD = 30.0;
    private static final double GTK_MULTIPLIER = 10.0;

    @Override
    public Mono<AgrometricalData> calculateHistoricalMetrics(
            Double lat, Double lon, String startDate, String endDate
    ) {
        log.info("Calculating historical agro metrics for period {} to {} at location: lat={}, lon={}",
                startDate, endDate, lat, lon);

        return openMeteoService.getWeather(lat, lon, WeatherRequestType.HISTORIC, null, startDate, endDate)
                .map(this::calculateMetricsFromResponse)
                .doOnSuccess(metrics -> log.info("Historical metrics calculated successfully: GTK={}, stressLevel={}",
                        metrics.gtk(), metrics.stressLevel()))
                .doOnError(e -> log.error("Failed to calculate historical metrics: {}", e.getMessage()));
    }

    @Override
    public Mono<AgrometricalData> calculateForecastMetrics(Double lat, Double lon, Integer days) {
        log.info("Calculating forecast agro metrics for {} days at location: lat={}, lon={}",
                days, lat, lon);

        return openMeteoService.getWeather(lat, lon, WeatherRequestType.FORECAST, days, null, null)
                .map(this::calculateMetricsFromResponse)
                .doOnSuccess(metrics -> log.info("Forecast metrics calculated successfully: GTK={}, stressLevel={}",
                        metrics.gtk(), metrics.stressLevel()))
                .doOnError(e -> log.error("Failed to calculate forecast metrics: {}", e.getMessage()));
    }

    @Override
    public AgrometricalData calculateMetricsFromResponse(OpenMeteoResponse response) {
        log.debug("Processing weather response with {} daily records",
                response.daily() != null && response.daily().time() != null ? response.daily().time().size() : 0);

        if (response.daily() == null) {
            log.warn("No daily data in response, returning empty metrics");
            return createEmptyMetrics();
        }

        List<Double> temperatures = response.daily().temperatureMax();
        List<Double> precipitations = response.daily().precipitationSum();

        if (temperatures == null || temperatures.isEmpty()) {
            log.warn("No temperature data available, returning empty metrics");
            return createEmptyMetrics();
        }

        double sumEffectiveTemp = 0.0;
        double sumPrecipitation = 0.0;
        int heatStressDays = 0;
        double minTemp = Double.MAX_VALUE;

        for (int i = 0; i < temperatures.size(); i++) {
            double temp = temperatures.get(i);
            double rain = (precipitations != null && i < precipitations.size())
                    ? precipitations.get(i) : 0.0;

            // 1. Поиск минимальной температуры (для определения заморозков)
            if (temp < minTemp) {
                minTemp = temp;
            }

            // 2. Подсчет дней теплового стресса (T > 30°C)
            if (temp > HEAT_STRESS_THRESHOLD) {
                heatStressDays++;
            }

            // 3. Расчет по формуле Селянинова: учитываются только дни с T > 10°C
            if (temp > ACTIVE_TEMP_THRESHOLD) {
                sumEffectiveTemp += temp;
                sumPrecipitation += rain;
            }
        }

        // Расчет ГТК (Гидротермический коэффициент)
        // Формула: ГТК = (Сумма осадков * 10) / Сумма активных температур
        double gtk = calculateGtk(sumPrecipitation, sumEffectiveTemp);
        String stressLevel = interpretGtk(gtk);

        log.debug("Metrics calculated: GTK={}, sumPrecip={}, sumEffTemp={}, heatDays={}, minTemp={}",
                gtk, sumPrecipitation, sumEffectiveTemp, heatStressDays, minTemp);

        return new AgrometricalData(
                gtk,
                sumPrecipitation,
                sumEffectiveTemp,
                heatStressDays,
                minTemp,
                stressLevel
        );
    }

    /**
     * Расчет гидротермического коэффициента Селянинова
     */
    private double calculateGtk(double sumPrecipitation, double sumEffectiveTemp) {
        if (sumEffectiveTemp <= 0) {
            return 0.0;
        }
        return (sumPrecipitation * GTK_MULTIPLIER) / sumEffectiveTemp;
    }



     @Override
    public Mono<AgrometricalData> calculateAveragedMetrics(
            Double lat, Double lon, String cropStartDate, Integer durationDays, Integer yearsCount) {

        log.info("Calculating averaged metrics: lat={}, lon={}, cropStart={}, duration={} days, years={}",
                lat, lon, cropStartDate, durationDays, yearsCount);

        // Валидация количества лет
        if (yearsCount < 1 || yearsCount > 5) {
            log.error("Invalid years count: {}. Must be between 1 and 5", yearsCount);
            return Mono.error(new IllegalArgumentException("Years count must be between 1 and 5"));
        }

        // Парсим дату начала (MM-dd)
        String[] parts = cropStartDate.split("-");
        if (parts.length != 2) {
            log.error("Invalid crop start date format: {}. Expected MM-dd", cropStartDate);
            return Mono.error(new IllegalArgumentException("Invalid crop start date format. Expected MM-dd"));
        }

        try {
            int month = Integer.parseInt(parts[0]);
            int day = Integer.parseInt(parts[1]);
            int currentYear = java.time.LocalDate.now().getYear();

            // Получаем данные за несколько лет и усредняем
            return reactor.core.publisher.Flux.range(0, yearsCount)
                    .flatMap(offset -> {
                        int referenceYear = currentYear - 1 - offset;
                        java.time.LocalDate startDate = java.time.LocalDate.of(referenceYear, month, day);
                        java.time.LocalDate endDate = startDate.plusDays(durationDays);

                        log.debug("Fetching data for year {} (period: {} to {})",
                                referenceYear, startDate, endDate);

                        return calculateHistoricalMetrics(
                                lat, lon, startDate.toString(), endDate.toString())
                                .onErrorResume(e -> {
                                    log.warn("Failed to fetch data for year {}: {}",
                                            referenceYear, e.getMessage());
                                    return Mono.empty();
                                });
                    })
                    .collectList()
                    .map(metricsList -> {
                        if (metricsList.isEmpty()) {
                            log.error("No data retrieved for any year");
                            throw new RuntimeException("No historical data available");
                        }

                        // Вычисляем средние значения
                        double avgGtk = metricsList.stream()
                                .mapToDouble(AgrometricalData::gtk)
                                .average().orElse(0.0);

                        double avgPrecipitation = metricsList.stream()
                                .mapToDouble(AgrometricalData::sumPrecipitation)
                                .average().orElse(0.0);

                        double avgEffectiveTemp = metricsList.stream()
                                .mapToDouble(AgrometricalData::sumEffectiveTemp)
                                .average().orElse(0.0);

                        int avgHeatStressDays = (int) metricsList.stream()
                                .mapToInt(AgrometricalData::heatStressDays)
                                .average().orElse(0.0);

                        double avgMinTemp = metricsList.stream()
                                .mapToDouble(AgrometricalData::minTempRecord)
                                .average().orElse(0.0);

                        // Определяем стресс-уровень по среднему ГТК
                        String stressLevel = interpretGtk(avgGtk)
                                + " (среднее за " + metricsList.size() + " лет)";

                        log.info("Calculated average metrics over {} years: GTK={}, stress={}",
                                metricsList.size(), avgGtk, stressLevel);

                        return new AgrometricalData(
                                avgGtk,
                                avgPrecipitation,
                                avgEffectiveTemp,
                                avgHeatStressDays,
                                avgMinTemp,
                                stressLevel
                        );
                    })
                    .doOnError(e -> log.error("Failed to calculate averaged metrics: {}", e.getMessage()));

        } catch (NumberFormatException e) {
            log.error("Invalid date format: {}", cropStartDate);
            return Mono.error(new IllegalArgumentException("Invalid date format in crop_start"));
        }
    }

    @Override
    public String interpretGtk(double gtk) {
        if (gtk == 0.0) return "Нет данных (T < 10°C)";
        if (gtk < 0.6) return "Очень сильная засуха";
        if (gtk < 1.0) return "Засушливо";
        if (gtk < 1.3) return "Оптимальное увлажнение";
        if (gtk < 1.6) return "Избыточное увлажнение";
        return "Переувлажнение / Риск гниения";
    }

    /**
     * Создает пустой объект метрик для случаев, когда данные недоступны
     */
    private AgrometricalData createEmptyMetrics() {
        return new AgrometricalData(0.0, 0.0, 0.0, 0, 0.0, "Нет данных");
    }
}