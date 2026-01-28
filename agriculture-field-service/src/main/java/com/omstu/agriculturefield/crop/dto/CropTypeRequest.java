package com.omstu.agriculturefield.crop.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CropTypeRequest(
        @NotBlank(message = "Название культуры обязательно")
        @Size(max = 100, message = "Название не должно превышать 100 символов")
        String name,

        @NotBlank(message = "Название категории культуры обязательно")
        @Size(max = 100, message = "Название не должно превышать 100 символов")
        String category, // Зерновые, Бобовые (пока больше не будет)

        Integer growingSeasonDays, // Средняя продолжительность вегетации (дней)

        BigDecimal optimalTemperatureMin, // Минимальная оптимальная температура

        BigDecimal optimalTemperatureMax, // Максимальная оптимальная температура

        BigDecimal waterRequirementsMm, // Требования к поливу (мм за сезон)

        String notes // Заметки агронома либо описание
) {

}
