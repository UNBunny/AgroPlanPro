package com.omstu.agriculturefield.crop.dto;

import com.omstu.agriculturefield.crop.model.CropType;
import com.omstu.agriculturefield.disease.model.DiseaseResistance;
import com.omstu.agriculturefield.crop.model.enums.ToleranceLevel;

import java.math.BigDecimal;
import java.util.Set;

public record CropVarietyResponse(

        Long id,

        String name,

        CropType cropType,

        String seedProducer, // Производитель семян

        Integer maturationDays, // Среднее количество дней до созревания

        Set<DiseaseResistance> diseaseResistance, // Устойчивость к заболеваниям (уже рассчитана заранее)

        ToleranceLevel droughtTolerance, // Устойчивость к засухе

        ToleranceLevel frostTolerance, // Устойчивость к заморозкам

        BigDecimal recommendedSeedingRateKgPerHa, // Рекомендуемая норма высева

        BigDecimal seedCostPerKg, // Стоимость семян за кг

        Boolean isHybrid,

        String notes

) {
}
