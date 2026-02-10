package com.omstu.weatherservice.controller;

import com.omstu.weatherservice.dto.AgrometricalData;
import com.omstu.weatherservice.service.AgroMetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST контроллер для работы с агрометеорологическими данными
 * Предоставляет эндпоинты для расчета агрометрик на основе исторических данных и прогнозов
 */
@RestController
@RequestMapping("/api/agro-data")
@RequiredArgsConstructor
@Slf4j
public class AgroMetricsController {

    private final AgroMetricsService agroMetricsService;

    /**
     * Получить агрометеорологические метрики за исторический период
     * Рассчитывает ГТК, тепловой стресс и другие показатели на основе фактических данных
     *
     * @param lat       широта
     * @param lon       долгота
     * @param startDate начальная дата периода (формат: yyyy-MM-dd, не ранее 2016-01-01)
     * @param endDate   конечная дата периода (формат: yyyy-MM-dd)
     * @return агрометеорологические метрики
     */
    @GetMapping("/metrics")
    public Mono<ResponseEntity<AgrometricalData>> getHistoricalMetrics(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam("start_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String startDate,
            @RequestParam("end_date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) String endDate
    ) {
        log.info("Received historical agro metrics request: lat={}, lon={}, period={} to {}", 
                lat, lon, startDate, endDate);

        return agroMetricsService.calculateHistoricalMetrics(lat, lon, startDate, endDate)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Historical agro metrics request completed successfully"))
                .onErrorResume(e -> {
                    log.error("Historical agro metrics request failed: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    /**
     * Получить прогноз агрометеорологических метрик на будущий период
     * Рассчитывает ожидаемые агрометрики на основе прогноза погоды
     *
     * @param lat  широта
     * @param lon  долгота
     * @param days количество дней прогноза (макс. 16)
     * @return прогнозные агрометеорологические метрики
     */
    @GetMapping("/forecast")
    public Mono<ResponseEntity<AgrometricalData>> getForecastMetrics(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam Integer days
    ) {
        log.info("Received forecast agro metrics request: lat={}, lon={}, days={}", 
                lat, lon, days);

        return agroMetricsService.calculateForecastMetrics(lat, lon, days)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Forecast agro metrics request completed successfully"))
                .onErrorResume(e -> {
                    log.error("Forecast agro metrics request failed: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    /**
     * Получить агрометрики для вегетационного периода следующего года
     *
     * ⚠️ ВАЖНО: Open-Meteo дает прогноз только на 16 дней вперед!
     * Для планирования вегетационного периода (90-120 дней) используются
     * ИСТОРИЧЕСКИЕ ДАННЫЕ за аналогичный период прошлого года как статистический референс.
     *
     * Например, для планирования посева в мае 2027 используются фактические данные за май-сентябрь 2025 года.
     *
     * @param lat           широта
     * @param lon           долгота
     * @param cropStartDate предполагаемая дата начала вегетации (формат: MM-dd, например: 05-15)
     * @param durationDays  продолжительность вегетационного периода в днях
     * @return агрометеорологические метрики на основе исторических данных прошлого года
     */
    @GetMapping("/next-season")
    public Mono<ResponseEntity<AgrometricalData>> getNextSeasonMetrics(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam("crop_start") String cropStartDate,
            @RequestParam("duration") Integer durationDays
    ) {
        log.info("Received next season agro metrics request: lat={}, lon={}, cropStart={}, duration={} days",
                lat, lon, cropStartDate, durationDays);

        // Используем один прошлый год
        return agroMetricsService.calculateAveragedMetrics(lat, lon, cropStartDate, durationDays, 1)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Next season agro metrics request completed successfully"))
                .onErrorResume(e -> {
                    log.error("Next season agro metrics request failed: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    /**
     * Получить усредненные агрометрики за несколько лет для более точного прогноза
     *
     * Рассчитывает среднее значение агрометрик за указанное количество лет.
     * Это более надежный подход, чем использование только одного прошлого года,
     * так как сглаживает аномальные климатические годы.
     *
     * Например, для планирования посева в мае 2027:
     * - Берутся данные за май-сентябрь 2022, 2023, 2024, 2025 (4 года)
     * - Рассчитывается среднее ГТК, средние осадки, среднее количество дней стресса
     *
     * @param lat           широта
     * @param lon           долгота
     * @param cropStartDate предполагаемая дата начала вегетации (формат: MM-dd)
     * @param durationDays  продолжительность вегетационного периода в днях
     * @param yearsCount    количество лет для усреднения (по умолчанию 3, макс 5)
     * @return усредненные агрометеорологические метрики
     */
    @GetMapping("/next-season-average")
    public Mono<ResponseEntity<AgrometricalData>> getNextSeasonAverageMetrics(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam("crop_start") String cropStartDate,
            @RequestParam("duration") Integer durationDays,
            @RequestParam(value = "years", defaultValue = "3") Integer yearsCount
    ) {
        log.info("Received averaged next season agro metrics request: lat={}, lon={}, cropStart={}, duration={} days, years={}",
                lat, lon, cropStartDate, durationDays, yearsCount);

        return agroMetricsService.calculateAveragedMetrics(lat, lon, cropStartDate, durationDays, yearsCount)
                .map(ResponseEntity::ok)
                .doOnSuccess(response -> log.info("Averaged next season metrics request completed successfully"))
                .onErrorResume(e -> {
                    log.error("Averaged next season metrics request failed: {}", e.getMessage());
                    return Mono.just(ResponseEntity.badRequest().build());
                });
    }

    /**
     * Обработчик ошибок валидации
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> handleValidationException(IllegalArgumentException e) {
        log.warn("Validation error: {}", e.getMessage());
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}

