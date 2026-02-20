package com.omstu.agriculturefield.disease.service.impl;

import com.omstu.agriculturefield.crop.model.CropHistory;
import com.omstu.agriculturefield.crop.repository.CropHistoryRepository;
import com.omstu.agriculturefield.disease.dto.DiseaseRiskItem;
import com.omstu.agriculturefield.disease.dto.DiseaseRiskResponse;
import com.omstu.agriculturefield.disease.dto.WeatherForecastWindowDto;
import com.omstu.agriculturefield.disease.model.enums.RiskLevel;
import com.omstu.agriculturefield.disease.service.DiseaseRiskForecastService;
import com.omstu.agriculturefield.field.model.AgriculturalField;
import com.omstu.agriculturefield.field.repository.AgriculturalFieldRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.locationtech.jts.geom.Point;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class DiseaseRiskForecastServiceImpl implements DiseaseRiskForecastService {

    private final AgriculturalFieldRepository fieldRepository;
    private final CropHistoryRepository cropHistoryRepository;

    @Qualifier("weatherWebClient")
    private final WebClient weatherWebClient;

    @Override
    @Transactional(readOnly = true)
    public DiseaseRiskResponse assessRisk(Long fieldId) {
        log.info("Assessing disease risk for fieldId={}", fieldId);

        AgriculturalField field = fieldRepository.findById(fieldId)
                .orElseThrow(() -> new RuntimeException("Field not found with id: " + fieldId));

        List<CropHistory> history = cropHistoryRepository.findByFieldIdOrderByPlantingDateDesc(fieldId);
        String cropName = history.isEmpty() ? "Неизвестно" : history.get(0).getCropType().getName();

        WeatherForecastWindowDto weather = fetchForecastWindow(field);

        List<DiseaseRiskItem> risks = new ArrayList<>();

        if (weather != null) {
            risks.add(assessLeafRust(weather));
            risks.add(assessSeptoria(weather));
            risks.add(assessFusarium(weather));
            risks.add(assessDownyMildew(weather));
            risks.add(assessAlternaria(weather));
        }

        risks.sort((a, b) -> b.riskLevel().compareTo(a.riskLevel()));

        return new DiseaseRiskResponse(fieldId, field.getFieldName(), cropName, LocalDate.now(), risks);
    }

    private WeatherForecastWindowDto fetchForecastWindow(AgriculturalField field) {
        Point centroid = field.getGeom().getCentroid();
        double lat = centroid.getY();
        double lon = centroid.getX();

        try {
            return weatherWebClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/api/agro-data/forecast-window")
                            .queryParam("lat", lat)
                            .queryParam("lon", lon)
                            .queryParam("days", 14)
                            .build())
                    .retrieve()
                    .bodyToMono(WeatherForecastWindowDto.class)
                    .block();
        } catch (Exception e) {
            log.warn("Failed to fetch forecast window for field {}: {}", field.getId(), e.getMessage());
            return null;
        }
    }

    /**
     * Бурая листовая ржавчина (Puccinia triticina).
     * Источник: Roelfs et al. (1992), оптимум 15-22°C + влажность >80%.
     */
    private DiseaseRiskItem assessLeafRust(WeatherForecastWindowDto w) {
        List<String> factors = new ArrayList<>();
        double score = 0.0;

        if (w.tempMean7d() != null && w.tempMean7d() >= 15 && w.tempMean7d() <= 22) {
            score += 0.45;
            factors.add(String.format("Средняя температура 7 дней: %.1f°C (оптимум 15-22°C)", w.tempMean7d()));
        } else if (w.tempMean7d() != null && w.tempMean7d() >= 12 && w.tempMean7d() <= 25) {
            score += 0.2;
            factors.add(String.format("Температура 7 дней: %.1f°C (допустимый диапазон)", w.tempMean7d()));
        }

        if (w.humidity7d() != null && w.humidity7d() >= 80) {
            score += 0.35;
            factors.add(String.format("Средняя влажность 7 дней: %.0f%% (порог ≥80%%)", w.humidity7d()));
        } else if (w.humidity7d() != null && w.humidity7d() >= 70) {
            score += 0.15;
            factors.add(String.format("Влажность 7 дней: %.0f%% (повышенная)", w.humidity7d()));
        }

        if (w.precip7d() != null && w.precip7d() >= 10) {
            score += 0.2;
            factors.add(String.format("Осадки за 7 дней: %.1f мм (порог ≥10 мм)", w.precip7d()));
        }

        RiskLevel level = scoreToRiskLevel(score);
        String recommendation = switch (level) {
            case CRITICAL, HIGH -> "Обработка фунгицидом (триазолы) в течение 3-5 дней";
            case MEDIUM -> "Мониторинг посевов каждые 2-3 дня";
            case LOW -> "Плановый осмотр";
        };

        return new DiseaseRiskItem("Бурая листовая ржавчина", "Puccinia triticina",
                level, Math.min(score, 1.0), factors, recommendation);
    }

    /**
     * Септориоз (Septoria tritici).
     * Источник: Shaw & Royle (1989), осадки >30 мм/10 дней + температура 15-20°C.
     */
    private DiseaseRiskItem assessSeptoria(WeatherForecastWindowDto w) {
        List<String> factors = new ArrayList<>();
        double score = 0.0;

        if (w.precip14d() != null && w.precip14d() >= 30) {
            score += 0.5;
            factors.add(String.format("Осадки за 14 дней: %.1f мм (порог ≥30 мм)", w.precip14d()));
        } else if (w.precip14d() != null && w.precip14d() >= 20) {
            score += 0.25;
            factors.add(String.format("Осадки за 14 дней: %.1f мм (умеренные)", w.precip14d()));
        }

        if (w.tempMean7d() != null && w.tempMean7d() >= 15 && w.tempMean7d() <= 20) {
            score += 0.35;
            factors.add(String.format("Средняя температура: %.1f°C (оптимум 15-20°C)", w.tempMean7d()));
        } else if (w.tempMean7d() != null && w.tempMean7d() >= 10 && w.tempMean7d() <= 25) {
            score += 0.15;
        }

        if (w.humidity7d() != null && w.humidity7d() >= 75) {
            score += 0.15;
            factors.add(String.format("Влажность: %.0f%%", w.humidity7d()));
        }

        RiskLevel level = scoreToRiskLevel(score);
        String recommendation = switch (level) {
            case CRITICAL, HIGH -> "Обработка фунгицидом (стробилурины + триазолы) немедленно";
            case MEDIUM -> "Профилактическая обработка при продолжении дождей";
            case LOW -> "Наблюдение";
        };

        return new DiseaseRiskItem("Септориоз листьев", "Zymoseptoria tritici",
                level, Math.min(score, 1.0), factors, recommendation);
    }

    /**
     * Фузариоз колоса (Fusarium graminearum).
     * Источник: Rossi et al. (2003), осадки в период цветения + температура >15°C.
     */
    private DiseaseRiskItem assessFusarium(WeatherForecastWindowDto w) {
        List<String> factors = new ArrayList<>();
        double score = 0.0;

        if (w.tempMean7d() != null && w.tempMean7d() >= 15 && w.tempMean7d() <= 30) {
            score += 0.35;
            factors.add(String.format("Температура: %.1f°C (благоприятно для Fusarium)", w.tempMean7d()));
        }

        if (w.precip7d() != null && w.precip7d() >= 15) {
            score += 0.4;
            factors.add(String.format("Осадки за 7 дней: %.1f мм (риск в период цветения)", w.precip7d()));
        }

        if (w.humidity7d() != null && w.humidity7d() >= 85) {
            score += 0.25;
            factors.add(String.format("Влажность: %.0f%% (критически высокая)", w.humidity7d()));
        }

        RiskLevel level = scoreToRiskLevel(score);
        String recommendation = switch (level) {
            case CRITICAL -> "СРОЧНО: обработка фунгицидом (тебуконазол) — потери урожая неизбежны без обработки";
            case HIGH -> "Обработка фунгицидом (тебуконазол) в фазу цветения — КРИТИЧНО";
            case MEDIUM -> "Обработка при начале цветения";
            case LOW -> "Наблюдение";
        };

        return new DiseaseRiskItem("Фузариоз колоса", "Fusarium graminearum",
                level, Math.min(score, 1.0), factors, recommendation);
    }

    /**
     * Пероноспороз подсолнечника (Plasmopara halstedii).
     * Источник: Gulya et al. (1998), влажность >90% + температура 10-15°C.
     */
    private DiseaseRiskItem assessDownyMildew(WeatherForecastWindowDto w) {
        List<String> factors = new ArrayList<>();
        double score = 0.0;

        if (w.humidity7d() != null && w.humidity7d() >= 90) {
            score += 0.5;
            factors.add(String.format("Влажность: %.0f%% (порог ≥90%%)", w.humidity7d()));
        } else if (w.humidity7d() != null && w.humidity7d() >= 80) {
            score += 0.2;
        }

        if (w.tempMean7d() != null && w.tempMean7d() >= 10 && w.tempMean7d() <= 15) {
            score += 0.4;
            factors.add(String.format("Температура: %.1f°C (оптимум 10-15°C)", w.tempMean7d()));
        } else if (w.tempMean7d() != null && w.tempMean7d() >= 8 && w.tempMean7d() <= 20) {
            score += 0.15;
        }

        if (w.precip7d() != null && w.precip7d() >= 20) {
            score += 0.1;
            factors.add(String.format("Осадки: %.1f мм", w.precip7d()));
        }

        RiskLevel level = scoreToRiskLevel(score);
        String recommendation = switch (level) {
            case CRITICAL, HIGH -> "Обработка фунгицидом (металаксил) немедленно";
            case MEDIUM -> "Профилактическая обработка семян перед посевом";
            case LOW -> "Наблюдение";
        };

        return new DiseaseRiskItem("Пероноспороз", "Plasmopara halstedii",
                level, Math.min(score, 1.0), factors, recommendation);
    }

    /**
     * Альтернариоз подсолнечника (Alternaria helianthi).
     * Источник: Leiminger & Hausladen (2012), высокая температура + чередование сухо/влажно.
     */
    private DiseaseRiskItem assessAlternaria(WeatherForecastWindowDto w) {
        List<String> factors = new ArrayList<>();
        double score = 0.0;

        if (w.tempMax7d() != null && w.tempMax7d() >= 28) {
            score += 0.35;
            factors.add(String.format("Максимальная температура: %.1f°C (стресс растений)", w.tempMax7d()));
        }

        if (w.humidity7d() != null && w.precip7d() != null
                && w.humidity7d() >= 70 && w.precip7d() < 5) {
            score += 0.4;
            factors.add("Чередование высокой влажности воздуха и сухости почвы");
        }

        if (w.tempMean7d() != null && w.tempMean7d() >= 20 && w.tempMean7d() <= 30) {
            score += 0.25;
            factors.add(String.format("Температура: %.1f°C (благоприятно для Alternaria)", w.tempMean7d()));
        }

        RiskLevel level = scoreToRiskLevel(score);
        String recommendation = switch (level) {
            case CRITICAL, HIGH -> "Обработка фунгицидом (ипродион, хлороталонил)";
            case MEDIUM -> "Мониторинг нижних листьев";
            case LOW -> "Наблюдение";
        };

        return new DiseaseRiskItem("Альтернариоз", "Alternaria helianthi",
                level, Math.min(score, 1.0), factors, recommendation);
    }

    private RiskLevel scoreToRiskLevel(double score) {
        if (score >= 0.85) return RiskLevel.CRITICAL;
        if (score >= 0.60) return RiskLevel.HIGH;
        if (score >= 0.35) return RiskLevel.MEDIUM;
        return RiskLevel.LOW;
    }
}
