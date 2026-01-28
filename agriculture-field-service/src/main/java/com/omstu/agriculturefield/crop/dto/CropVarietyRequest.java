package com.omstu.agriculturefield.crop.dto;

import com.omstu.agriculturefield.crop.model.enums.ToleranceLevel;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.Set;

public record CropVarietyRequest(

        @NotNull
        String name,

        @NotNull
        @Positive
        Long cropTypeId,

        String seedProducer, // Производитель семян

        Integer maturationDays, // Среднее количество дней до созревания

        @NotEmpty
        Set<@NotNull @Positive Long> diseaseResistanceId, // Устойчивость к заболеваниям (уже рассчитана заранее)

        ToleranceLevel droughtTolerance, // Устойчивость к засухе

        ToleranceLevel frostTolerance, // Устойчивость к заморозкам

        BigDecimal recommendedSeedingRateKgPerHa, // Рекомендуемая норма высева

        BigDecimal seedCostPerKg, // Стоимость семян за кг

        Boolean isHybrid,

        String notes

) {
}
