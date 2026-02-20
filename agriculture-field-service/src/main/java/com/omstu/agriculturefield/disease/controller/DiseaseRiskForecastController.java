package com.omstu.agriculturefield.disease.controller;

import com.omstu.agriculturefield.disease.dto.DiseaseRiskResponse;
import com.omstu.agriculturefield.disease.service.DiseaseRiskForecastService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/fields")
@RequiredArgsConstructor
@Slf4j
public class DiseaseRiskForecastController {

    private final DiseaseRiskForecastService diseaseRiskForecastService;

    @GetMapping("/{fieldId}/disease-risk")
    public DiseaseRiskResponse getDiseaseRisk(@PathVariable Long fieldId) {
        log.info("Assessing disease risk for fieldId={}", fieldId);
        return diseaseRiskForecastService.assessRisk(fieldId);
    }
}
