package com.omstu.weatherservice.service.utils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Утилита для работы с датами и периодами
 */
public final class DateUtils {

    private DateUtils() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * Определяет, является ли период длинным (больше заданного количества месяцев)
     *
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @param thresholdMonths порог в месяцах
     * @return true если период длинный
     */
    public static boolean isLongPeriod(LocalDate startDate, LocalDate endDate, int thresholdMonths) {
        long monthsBetween = ChronoUnit.MONTHS.between(startDate, endDate);
        return monthsBetween > thresholdMonths;
    }

    /**
     * Разбивает период на интервалы по месяцам
     *
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return список интервалов
     */
    public static List<DateRange> splitByMonths(LocalDate startDate, LocalDate endDate) {
        List<DateRange> ranges = new ArrayList<>();

        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            YearMonth yearMonth = YearMonth.from(current);
            LocalDate monthStart = current;
            LocalDate monthEnd = yearMonth.atEndOfMonth().isBefore(endDate) ?
                    yearMonth.atEndOfMonth() : endDate;

            ranges.add(new DateRange(monthStart, monthEnd));

            current = monthEnd.plusDays(1);
        }

        return ranges;
    }

    /**
     * Разбивает период на интервалы по 3 месяца (оптимально для исторических данных)
     *
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return список интервалов
     */
    public static List<DateRange> splitByThreeMonths(LocalDate startDate, LocalDate endDate) {
        List<DateRange> ranges = new ArrayList<>();

        LocalDate current = startDate;

        while (!current.isAfter(endDate)) {
            LocalDate threeMonthsLater = current.plusMonths(3).minusDays(1);
            LocalDate rangeEnd = threeMonthsLater.isBefore(endDate) ? threeMonthsLater : endDate;

            ranges.add(new DateRange(current, rangeEnd));

            current = rangeEnd.plusDays(1);
        }

        return ranges;
    }

    /**
     * Представляет временной интервал
     */
    public record DateRange(LocalDate startDate, LocalDate endDate) {
        @Override
        public String toString() {
            return startDate + " to " + endDate;
        }
    }
}
