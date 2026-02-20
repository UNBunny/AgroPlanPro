package com.omstu.agriculturefield.rotation.service.impl;

import com.omstu.agriculturefield.crop.model.CropHistory;
import com.omstu.agriculturefield.crop.model.CropType;
import com.omstu.agriculturefield.crop.repository.CropHistoryRepository;
import com.omstu.agriculturefield.crop.repository.CropTypeRepository;
import com.omstu.agriculturefield.field.model.AgriculturalField;
import com.omstu.agriculturefield.field.repository.AgriculturalFieldRepository;
import com.omstu.agriculturefield.rotation.dto.CropRecommendationItem;
import com.omstu.agriculturefield.rotation.dto.CropRecommendationResponse;
import com.omstu.agriculturefield.rotation.dto.PricePredictionResponse;
import com.omstu.agriculturefield.rotation.dto.SeasonalWeatherDto;
import com.omstu.agriculturefield.rotation.dto.YieldPredictionRequest;
import com.omstu.agriculturefield.rotation.dto.YieldPredictionResponse;
import com.omstu.agriculturefield.rotation.model.CropRotationRule;
import com.omstu.agriculturefield.rotation.repository.CropRotationRuleRepository;
import com.omstu.agriculturefield.rotation.service.RotationRecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RotationRecommendationServiceImpl implements RotationRecommendationService {

    private static final Map<String, Double> CROP_COSTS_RUB_PER_HA = Map.of(
            "Пшеница озимая", 18000.0,
            "Пшеница яровая", 14000.0,
            "Ячмень яровой", 12000.0,
            "Подсолнечник", 22000.0,
            "Кукуруза", 25000.0,
            "Соя", 20000.0,
            "Рапс", 19000.0,
            "Горох", 13000.0
    );

    private final AgriculturalFieldRepository fieldRepository;
    private final CropHistoryRepository cropHistoryRepository;
    private final CropTypeRepository cropTypeRepository;
    private final CropRotationRuleRepository cropRotationRuleRepository;

    @Qualifier("weatherWebClient")
    private final WebClient weatherWebClient;

    @Qualifier("mlWebClient")
    private final WebClient mlWebClient;

    @Override
    @Transactional(readOnly = true)
    public CropRecommendationResponse getRecommendations(Long fieldId, Integer targetYear) {
        log.info("Building recommendations for fieldId={}, targetYear={}", fieldId, targetYear);

        AgriculturalField field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new RuntimeException("Field not found with id: " + fieldId));

        List<CropHistory> history = cropHistoryRepository.findByFieldIdOrderByPlantingDateDesc(fieldId);

        SeasonalWeatherDto weather = fetchSeasonalWeather(field, targetYear);

        List<CropType> allCropTypes = cropTypeRepository.findAll();

        CropType lastCrop = history.isEmpty() ? null : history.get(0).getCropType();

        List<CropRecommendationItem> items = new ArrayList<>();
        for (CropType cropType : allCropTypes) {
            RotationViolation violation = checkRotationViolation(lastCrop, cropType, history, targetYear);

            Double predictedYield = fetchPredictedYield(field, cropType, weather);
            Double predictedPrice = fetchPredictedPrice(field, cropType, targetYear);

            double estimatedProfit = 0.0;
            if (predictedYield != null && predictedPrice != null) {
                double costs = CROP_COSTS_RUB_PER_HA.getOrDefault(cropType.getName(), 15000.0);
                estimatedProfit = predictedYield * predictedPrice * 0.1 - costs;
            }

            items.add(new CropRecommendationItem(
                    cropType.getId(),
                    cropType.getName(),
                    violation == null,
                    violation != null ? violation.reason() : null,
                    predictedYield,
                    predictedPrice,
                    estimatedProfit,
                    0
            ));
        }

        List<CropRecommendationItem> ranked = rankItems(items);

        return new CropRecommendationResponse(fieldId, field.getFieldName(), targetYear, ranked);
    }

    private SeasonalWeatherDto fetchSeasonalWeather(AgriculturalField field, Integer targetYear) {
        Point centroid = field.getGeom().getCentroid();
        double lat = centroid.getY();
        double lon = centroid.getX();
        int harvestYear = targetYear - 1;

        try {
            return weatherWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/agro-data/seasonal")
                            .queryParam("lat", lat)
                            .queryParam("lon", lon)
                            .queryParam("year", harvestYear)
                            .build())
                    .retrieve()
                    .bodyToMono(SeasonalWeatherDto.class)
                    .block();
        } catch (Exception e) {
            log.warn("Failed to fetch seasonal weather for field {}: {}", field.getId(), e.getMessage());
            return null;
        }
    }

    private Double fetchPredictedYield(AgriculturalField field, CropType cropType, SeasonalWeatherDto weather) {
        if (weather == null) {
            return null;
        }
        Point centroid = field.getGeom().getCentroid();
        String region = resolveRegionName(centroid.getY(), centroid.getX());

        YieldPredictionRequest request = new YieldPredictionRequest(
                region,
                cropType.getName(),
                weather.precipOctMar(),
                weather.minTempWinter(),
                weather.precipAprMay(),
                weather.tempSumAprMay(),
                weather.frostRiskSpring(),
                weather.precipJunJul(),
                weather.tempSumJunJul(),
                weather.heatStressJunJul(),
                weather.gtkJunJul(),
                weather.precipAugSep(),
                weather.tempSumAugSep(),
                weather.heatStressAugSep(),
                weather.gtkAprSep(),
                weather.tempSumAprSep(),
                weather.totalHeatStressDays()
        );

        try {
            YieldPredictionResponse response = mlWebClient.post()
                    .uri("/predict/yield")
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(YieldPredictionResponse.class)
                    .block();
            return response != null ? response.predictedYield() : null;
        } catch (Exception e) {
            log.warn("Failed to fetch yield prediction for crop {}: {}", cropType.getName(), e.getMessage());
            return null;
        }
    }

    private Double fetchPredictedPrice(AgriculturalField field, CropType cropType, Integer targetYear) {
        Point centroid = field.getGeom().getCentroid();
        String region = resolveRegionName(centroid.getY(), centroid.getX());

        try {
            PricePredictionResponse response = mlWebClient.post()
                    .uri("/predict")
                    .bodyValue(Map.of(
                            "city", region,
                            "region", region,
                            "crop", cropType.getName(),
                            "month", 7,
                            "day_of_year", 180,
                            "year", targetYear
                    ))
                    .retrieve()
                    .bodyToMono(PricePredictionResponse.class)
                    .block();
            return response != null ? response.predictedPrice() : null;
        } catch (Exception e) {
            log.warn("Failed to fetch price prediction for crop {}: {}", cropType.getName(), e.getMessage());
            return null;
        }
    }

    private RotationViolation checkRotationViolation(CropType lastCrop, CropType candidate,
                                                      List<CropHistory> history, Integer targetYear) {
        if (lastCrop == null) {
            return null;
        }

        List<CropRotationRule> rules = cropRotationRuleRepository.findByPredecessorCropId(lastCrop.getId());

        Optional<CropRotationRule> matchingRule = rules.stream()
                .filter(r -> r.getSuccessorCrop().getId().equals(candidate.getId()))
                .findFirst();

        if (matchingRule.isPresent()) {
            CropRotationRule rule = matchingRule.get();
            if (Boolean.FALSE.equals(rule.getAllowed())) {
                return new RotationViolation(rule.getReason() != null
                        ? rule.getReason()
                        : "Запрещено правилами севооборота после " + lastCrop.getName());
            }
        }

        int yearsWithSameCrop = 0;
        for (CropHistory h : history) {
            if (h.getCropType().getId().equals(candidate.getId())) {
                int plantYear = toLocalDate(h.getPlantingDate()).getYear();
                if (targetYear - plantYear <= 4) {
                    yearsWithSameCrop++;
                }
            }
        }

        if (yearsWithSameCrop >= 2) {
            return new RotationViolation("Культура повторяется более 2 лет подряд — риск накопления болезней");
        }

        return null;
    }

    private List<CropRecommendationItem> rankItems(List<CropRecommendationItem> items) {
        List<CropRecommendationItem> sorted = items.stream()
                .sorted(Comparator
                        .comparing(CropRecommendationItem::rotationCompliant).reversed()
                        .thenComparing(Comparator.comparingDouble(
                                (CropRecommendationItem i) -> i.estimatedProfitRubPerHa() != null ? i.estimatedProfitRubPerHa() : Double.MIN_VALUE
                        ).reversed()))
                .toList();

        List<CropRecommendationItem> ranked = new ArrayList<>();
        for (int i = 0; i < sorted.size(); i++) {
            CropRecommendationItem item = sorted.get(i);
            ranked.add(new CropRecommendationItem(
                    item.cropTypeId(),
                    item.cropTypeName(),
                    item.rotationCompliant(),
                    item.rotationViolationReason(),
                    item.predictedYieldCentnersPerHa(),
                    item.predictedPriceRubPerTon(),
                    item.estimatedProfitRubPerHa(),
                    i + 1
            ));
        }
        return ranked;
    }

    private String resolveRegionName(double lat, double lon) {
        return "Омская область";
    }

    private LocalDate toLocalDate(Date date) {
        if (date == null) return LocalDate.now();
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private record RotationViolation(String reason) {}
}
