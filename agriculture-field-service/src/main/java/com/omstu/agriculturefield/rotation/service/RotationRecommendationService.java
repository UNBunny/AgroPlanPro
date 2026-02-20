package com.omstu.agriculturefield.rotation.service;

import com.omstu.agriculturefield.rotation.dto.CropRecommendationResponse;

public interface RotationRecommendationService {

    CropRecommendationResponse getRecommendations(Long fieldId, Integer targetYear);
}
