package com.omstu.weatherservice.service.strategy;

import com.omstu.weatherservice.dto.OpenMeteoResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

/**
 * Стратегия для выполнения запросов к погодному API
 */
public interface WeatherRequestStrategy {

    /**
     * Выполняет запрос к погодному API
     *
     * @param webClient веб-клиент для запроса
     * @param lat       широта
     * @param lon       долгота
     * @return ответ от API
     */
    Mono<OpenMeteoResponse> execute(WebClient webClient, Double lat, Double lon);

    /**
     * Возвращает тип стратегии
     */
    String getType();
}

