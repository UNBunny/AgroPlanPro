package com.omstu.agriculturefield.dto.crop;

import java.math.BigDecimal;

public record CropTypeResponse(
        Long id,

        String name,

        String category, // Зерновые, Бобовые (пока больше не будет)

        Integer growingSeasonDays, // Средняя продолжительность вегетации (дней)

        BigDecimal optimalTemperatureMin, // Минимальная оптимальная температура

        BigDecimal optimalTemperatureMax, // Максимальная оптимальная температура

        BigDecimal waterRequirementsMm, // Требования к поливу (мм за сезон)

        String notes // Заметки агронома либо описание
) {
}