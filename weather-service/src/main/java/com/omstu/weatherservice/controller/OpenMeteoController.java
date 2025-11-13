package com.omstu.weatherservice.controller;

import com.omstu.weatherservice.dto.OpenMeteoResponse;
import com.omstu.weatherservice.dto.WeatherRequestType;
import com.omstu.weatherservice.service.impl.OpenMeteoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/open-meteo")
public class OpenMeteoController {

    private final OpenMeteoService openMeteoService;

    public OpenMeteoController(OpenMeteoService openMeteoService) {
        this.openMeteoService = openMeteoService;
    }

    @GetMapping("/ml")
    public Mono<OpenMeteoResponse> getWeather(@RequestParam Double lat, @RequestParam Double lon, @RequestParam Integer days) {
        return openMeteoService.getWeather(lat, lon, WeatherRequestType.FORECAST, days, null, null);
    }

    @GetMapping("/ml/historic-data")
    public Mono<OpenMeteoResponse> getHistoricWeather(
            @RequestParam Double lat, @RequestParam Double lon,
            @RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate) {
        return openMeteoService.getWeather(lat, lon, WeatherRequestType.HISTORIC, null, startDate, endDate);
    }
}
