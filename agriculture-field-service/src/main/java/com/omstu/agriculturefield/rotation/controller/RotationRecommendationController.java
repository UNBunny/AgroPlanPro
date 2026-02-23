package com.omstu.agriculturefield.rotation.controller;

import com.omstu.agriculturefield.rotation.dto.CropRecommendationResponse;
import com.omstu.agriculturefield.rotation.service.RotationRecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Year;

@RestController
@RequestMapping("/api/fields")
@RequiredArgsConstructor
@Slf4j
public class RotationRecommendationController {

    private final RotationRecommendationService rotationRecommendationService;

    @GetMapping("/{fieldId}/recommendations")
    public CropRecommendationResponse getRecommendations(
            @PathVariable Long fieldId,
            @RequestParam(required = false) Integer year
    ) {
        int targetYear = year != null ? year : Year.now().getValue() + 1;
        log.info("Fetching crop recommendations for fieldId={}, targetYear={}", fieldId, targetYear);
        return rotationRecommendationService.getRecommendations(fieldId, targetYear);
    }
}
