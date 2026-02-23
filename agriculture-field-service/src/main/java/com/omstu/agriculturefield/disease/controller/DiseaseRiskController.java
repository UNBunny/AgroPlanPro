package com.omstu.agriculturefield.disease.controller;

import com.omstu.agriculturefield.disease.dto.DiseaseRiskResponse;
import com.omstu.agriculturefield.disease.service.impl.DiseaseRiskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;


/**
 * REST контроллер для оценки агрономических рисков полей.
 *
 * Эндпоинт: GET /api/fields/{id}/disease-risk?crop=пшеница
 *
 * Возвращает:
 * - Общий уровень риска
 * - Риск засухи (на основе ГТК)
 * - Риск заморозков
 * - Риск теплового стресса
 * - Риски конкретных болезней (rule-based)
 * - Рекомендации по профилактике и лечению
 */
@RestController
@RequestMapping("/api/fields")
@RequiredArgsConstructor
@Slf4j
public class DiseaseRiskController {

    private final DiseaseRiskService diseaseRiskService;

    /**
     * Оценить агрономические риски для поля.
     *
     * @param fieldId ID поля
     * @param crop    Название культуры (например: "пшеница", "ячмень", "кукуруза")
     * @return Полная оценка рисков с рекомендациями
     */
    @GetMapping("/{fieldId}/disease-risk")
    public Mono<ResponseEntity<DiseaseRiskResponse>> getDiseaseRisk(
            @PathVariable Long fieldId,
            @RequestParam(defaultValue = "пшеница") String crop
    ) {
        log.info("Запрос оценки рисков для поля {} с культурой '{}'", fieldId, crop);

        try {
            com.omstu.agriculturefield.disease.model.enums.SupportedCrop.fromRussianName(crop);
        } catch (IllegalArgumentException e) {
            log.warn("Неподдерживаемая культура: {}", crop);
            return Mono.just(ResponseEntity.badRequest().build());
        }

        return diseaseRiskService.assessFieldRisk(fieldId, crop)
                .map(ResponseEntity::ok)
                .doOnSuccess(resp -> log.info("Оценка рисков для поля {} завершена: общий уровень = {}",
                        fieldId, resp.getBody() != null ? resp.getBody().overallRiskLevel() : "N/A"))
                .onErrorResume(RuntimeException.class, ex -> {
                    log.error("Ошибка оценки рисков для поля {}: {}", fieldId, ex.getMessage());
                    return Mono.just(ResponseEntity.notFound().build());
                });
    }
}

