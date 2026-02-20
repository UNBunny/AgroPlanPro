package com.omstu.weatherservice.dto;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Сезонные агрометеорологические данные для года урожая.
 * Содержит разбивку по периодам, важным для сельского хозяйства:
 * - Октябрь-Март: накопленная влага (важно для озимых)
 * - Апрель-Май: старт вегетации
 * - Июнь-Июль: критический период (колошение, цветение)
 * - Август-Сентябрь: созревание, уборка
 * - Апрель-Сентябрь: полный вегетационный период
 */
public record SeasonalAgrometricsResponse(
        // Год урожая
        Integer year,

        // === Октябрь-Март (накопленная влага для озимых) ===
        Double precipOctMar,       // Сумма осадков (мм)
        Double minTempWinter,      // Минимальная температура (риск вымерзания)

        // === Апрель-Май (старт вегетации) ===
        Double precipAprMay,       // Сумма осадков (мм)
        Double tempSumAprMay,      // Сумма эффективных температур (>10°C)
        Boolean frostRiskSpring,   // Были ли заморозки (min < 0)
        Double gtkAprMay,          // ГТК за период

        // === Июнь-Июль (критический период) ===
        Double precipJunJul,       // Сумма осадков (мм)
        Double tempSumJunJul,      // Сумма эффективных температур
        Integer heatStressJunJul,  // Дни с T > 30°C
        Integer extremeHeatJunJul, // Дни с T > 35°C
        Double avgTempJunJul,      // Средняя температура
        Double gtkJunJul,          // ГТК за период

        // === Август-Сентябрь (уборка, поздние культуры) ===
        Double precipAugSep,       // Сумма осадков (мм)
        Double tempSumAugSep,      // Сумма эффективных температур
        Integer heatStressAugSep,  // Дни с T > 30°C
        Double gtkAugSep,          // ГТК за период

        // === Полный вегетационный период (Апрель-Сентябрь) ===
        Double gtkAprSep,          // ГТК за весь сезон
        Double tempSumAprSep,      // Полная сумма эффективных температур
        Integer totalHeatStressDays, // Общее число дней жары (>30°C)
        Double minTempVegetation,  // Минимальная температура за вегетацию
        Integer longestDryPeriod   // Макс серия дней без осадков (<1 мм) за Apr-Sep
) {
    public SeasonalAgrometricsResponse {
        // Округляем все Double поля до 2 знаков
        precipOctMar = round(precipOctMar);
        minTempWinter = round(minTempWinter);
        precipAprMay = round(precipAprMay);
        tempSumAprMay = round(tempSumAprMay);
        gtkAprMay = round(gtkAprMay);
        precipJunJul = round(precipJunJul);
        tempSumJunJul = round(tempSumJunJul);
        avgTempJunJul = round(avgTempJunJul);
        gtkJunJul = round(gtkJunJul);
        precipAugSep = round(precipAugSep);
        tempSumAugSep = round(tempSumAugSep);
        gtkAugSep = round(gtkAugSep);
        gtkAprSep = round(gtkAprSep);
        tempSumAprSep = round(tempSumAprSep);
        minTempVegetation = round(minTempVegetation);
    }
    private static Double round(Double value) {
        if (value == null) return null;
        return BigDecimal.valueOf(value)
                .setScale(4, RoundingMode.HALF_UP)
                .doubleValue();
    }

}
