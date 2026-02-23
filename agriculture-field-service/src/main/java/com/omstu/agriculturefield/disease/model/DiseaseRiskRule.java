package com.omstu.agriculturefield.disease.model;

import com.omstu.agriculturefield.disease.model.enums.DiseaseType;
import com.omstu.agriculturefield.disease.model.enums.RiskLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Правило оценки риска болезни.
 * Определяет при каких погодных условиях возникает риск конкретной болезни.
 * Rule-based подход: если все условия правила выполняются — риск активируется.
 */
@Entity
@Table(name = "disease_risk_rules")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DiseaseRiskRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Название болезни (для которой правило) */
    @Column(nullable = false)
    private String diseaseName;

    /** Тип болезни */
    @Enumerated(EnumType.STRING)
    private DiseaseType diseaseType;

    /** Какие культуры поражает (через запятую, например: "пшеница,ячмень") */
    @Column(nullable = false)
    private String affectedCrops;

    /** Уровень риска, когда правило срабатывает */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RiskLevel riskLevel;

    /** Весовой коэффициент риска (0.0 - 1.0) */
    @Column(nullable = false)
    private Double riskWeight;

    // === Условия по температуре ===

    /** Мин. средняя температура для активации (°C), null = не проверять */
    private Double tempMinThreshold;

    /** Макс. средняя температура для активации (°C), null = не проверять */
    private Double tempMaxThreshold;

    // === Условия по осадкам ===

    /** Мин. осадки за 7 дней (мм), null = не проверять */
    private Double precipMin7d;

    /** Макс. осадки за 7 дней (мм), null = не проверять */
    private Double precipMax7d;

    // === Условия по влажности ===

    /** Мин. влажность (%), null = не проверять */
    private Double humidityMinThreshold;

    // === Условия по ГТК ===

    /** Мин. ГТК, null = не проверять */
    private Double gtkMin;

    /** Макс. ГТК, null = не проверять */
    private Double gtkMax;

    // === Условия по теплу ===

    /** Мин. дней теплового стресса (T>30°C), null = не проверять */
    private Integer heatStressDaysMin;

    // === Условия по засухе ===

    /** Мин. длина сухого периода (дни без осадков), null = не проверять */
    private Integer dryPeriodDaysMin;

    /** Активный сезон (месяцы, через запятую, например: "5,6,7,8") */
    private String activeSeason;

    /** Описание правила (человекочитаемое) */
    @Column(length = 500)
    private String ruleDescription;

    /** Рекомендация по профилактике */
    @Column(length = 1000)
    private String preventionAdvice;

    /** Рекомендация по лечению */
    @Column(length = 1000)
    private String treatmentAdvice;

    /** Через сколько дней принять меры (срочность) */
    private Integer urgencyDays;

    /** Правило активно? */
    @Column(nullable = false)
    private Boolean isActive = true;
}

