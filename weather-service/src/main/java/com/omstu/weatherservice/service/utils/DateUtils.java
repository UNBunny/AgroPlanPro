package com.omstu.weatherservice.service.utils;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

public class DateUtils {

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

    public record DateRange(LocalDate startDate, LocalDate endDate) {
        @Override
        public String toString() {
            return startDate + " to " + endDate;
        }
    }
}
