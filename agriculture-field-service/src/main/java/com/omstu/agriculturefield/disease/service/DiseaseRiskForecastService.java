package com.omstu.agriculturefield.disease.service;

import com.omstu.agriculturefield.disease.dto.DiseaseRiskResponse;

public interface DiseaseRiskForecastService {

    DiseaseRiskResponse assessRisk(Long fieldId);
}
