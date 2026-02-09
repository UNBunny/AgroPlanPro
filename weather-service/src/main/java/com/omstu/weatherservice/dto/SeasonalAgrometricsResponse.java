package com.omstu.weatherservice.dto;

/**
 * Сезонные агрометеорологические данные для года урожая.
 *
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

        // === Июнь-Июль (критический период) ===
        Double precipJunJul,       // Сумма осадков (мм)
        Double tempSumJunJul,      // Сумма эффективных температур
        Integer heatStressJunJul,  // Дни с T > 30°C
        Double gtkJunJul,          // ГТК за период

        // === Август-Сентябрь (уборка, поздние культуры) ===
        Double precipAugSep,       // Сумма осадков (мм)
        Double tempSumAugSep,      // Сумма эффективных температур
        Integer heatStressAugSep,  // Дни с T > 30°C

        // === Полный вегетационный период (Апрель-Сентябрь) ===
        Double gtkAprSep,          // ГТК за весь сезон
        Double tempSumAprSep,      // Полная сумма эффективных температур
        Integer totalHeatStressDays, // Общее число дней жары
        Double minTempVegetation   // Минимальная температура за вегетацию
) {}

