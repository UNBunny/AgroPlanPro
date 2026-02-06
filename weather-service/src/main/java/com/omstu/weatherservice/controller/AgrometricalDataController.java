package com.omstu.weatherservice.controller;


import com.omstu.weatherservice.dto.AgrometricalData;
import com.omstu.weatherservice.dto.OpenMeteoResponse;
import com.omstu.weatherservice.dto.WeatherRequestType;
import com.omstu.weatherservice.service.impl.AgrometricalDataService;
import com.omstu.weatherservice.service.impl.OpenMeteoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/agro-data")
@RequiredArgsConstructor
public class AgrometricalDataController {

    private final AgrometricalDataService agrometricalDataService;
    private final OpenMeteoService openMeteoService;

    @GetMapping("/metrics")
    public Mono<AgrometricalData> getAgrometricalData(
            @RequestParam Double lat, @RequestParam Double lon,
            @RequestParam("start_date") String startDate, @RequestParam("end_date") String endDate) {
        return openMeteoService.getWeather(lat, lon, WeatherRequestType.HISTORIC, null, startDate, endDate)
                .map(agrometricalDataService::calculateMetrics);
    }
}
