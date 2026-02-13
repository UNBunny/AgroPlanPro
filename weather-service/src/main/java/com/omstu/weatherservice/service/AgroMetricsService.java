package com.omstu.weatherservice.service;

import com.omstu.weatherservice.dto.AgrometricalData;
import com.omstu.weatherservice.dto.OpenMeteoResponse;
import reactor.core.publisher.Mono;

/**
 * Сервис для расчета агрометеорологических показателей
 */
public interface AgroMetricsService {

    /**
     * Рассчитывает агрометеорологические метрики на основе исторических данных
     *
     * @param lat широта
     * @param lon долгота
     * @param startDate начальная дата периода (формат: yyyy-MM-dd)
     * @param endDate конечная дата периода (формат: yyyy-MM-dd)
     * @return агрометеорологические данные
     */
    Mono<AgrometricalData> calculateHistoricalMetrics(Double lat, Double lon, String startDate, String endDate);

    /**
     * Прогнозирует агрометеорологические метрики на будущий период
     *
     * @param lat широта
     * @param lon долгота
     * @param days количество дней прогноза
     * @return прогнозные агрометеорологические данные
     */
    Mono<AgrometricalData> calculateForecastMetrics(Double lat, Double lon, Integer days);

    /**
     * Рассчитывает метрики из готового ответа Open-Meteo
     *
     * @param response ответ от Open-Meteo API
     * @return агрометеорологические данные
     */
    AgrometricalData calculateMetricsFromResponse(OpenMeteoResponse response);

    /**
     * Рассчитывает усредненные агрометрики за несколько лет для более точного прогноза
     *
     * @param lat широта
     * @param lon долгота
     * @param cropStartDate дата начала вегетации (формат: MM-dd)
     * @param durationDays продолжительность вегетационного периода в днях
     * @param yearsCount количество лет для усреднения (1-5)
     * @return усредненные агрометеорологические данные
     */
    Mono<AgrometricalData> calculateAveragedMetrics(
            Double lat, Double lon, String cropStartDate, Integer durationDays, Integer yearsCount);

    /**
     * Интерпретация значения ГТК
     *
     * @param gtk значение гидротермического коэффициента
     * @return текстовая интерпретация
     */
    String interpretGtk(double gtk);

    /**
     * Рассчитывает сезонные агрометрики для конкретного года урожая.
     *
     * Один запрос возвращает данные по всем важным периодам:
     * - Октябрь-Март: накопленная влага (для озимых)
     * - Апрель-Май: старт вегетации
     * - Июнь-Июль: критический период
     * - Август-Сентябрь: уборка/поздние культуры
     * - Апрель-Сентябрь: полный вегетационный период
     *
     * @param lat широта
     * @param lon долгота
     * @param year год урожая (например, 2023)
     * @return сезонные агрометеорологические данные
     */
    Mono<com.omstu.weatherservice.dto.SeasonalAgrometricsResponse> calculateSeasonalMetrics(
            Double lat, Double lon, Integer year);
}

