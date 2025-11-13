package com.omstu.weatherservice.mapper;

import com.omstu.weatherservice.dto.Daily;
import com.omstu.weatherservice.dto.Hourly;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DailyMapper {

    DailyMapper INSTANCE = Mappers.getMapper(DailyMapper.class);
    Logger logger = LoggerFactory.getLogger(DailyMapper.class);

    default Daily combineDailyList(List<Daily> dailyList, List<Hourly> hourlyList) {
        if (dailyList == null || dailyList.isEmpty()) {
            return null;
        }

        // Сначала собираем все daily данные
        List<String> combinedTime = new ArrayList<>();
        List<Double> combinedTemperatureMax = new ArrayList<>();
        List<Double> combinedTemperatureMin = new ArrayList<>();
        List<Double> combinedPrecipitationSum = new ArrayList<>();
        List<Double> combinedReferenceEvapotranspiration = new ArrayList<>();
        List<Double> combinedTemperatureMean = new ArrayList<>();
        List<Double> combinedRelativeHumidityMean = new ArrayList<>();
        List<Double> combinedRelativeHumidityMin = new ArrayList<>();
        List<Double> combinedWindSpeedMax = new ArrayList<>();
        List<Double> combinedWindGustsMax = new ArrayList<>();
        List<Double> combinedShortwaveRadiationSum = new ArrayList<>();
        List<Integer> combinedSunshineDuration = new ArrayList<>();

        for (Daily daily : dailyList) {
            if (daily != null) {
                combineLists(combinedTime, daily.time());
                combineLists(combinedTemperatureMax, daily.temperatureMax());
                combineLists(combinedTemperatureMin, daily.temperatureMin());
                combineLists(combinedPrecipitationSum, daily.precipitationSum());
                combineLists(combinedReferenceEvapotranspiration, daily.referenceEvapotranspiration());
                combineLists(combinedTemperatureMean, daily.temperatureMean());
                combineLists(combinedRelativeHumidityMean, daily.relativeHumidityMean());
                combineLists(combinedRelativeHumidityMin, daily.relativeHumidityMin());
                combineLists(combinedWindSpeedMax, daily.windSpeedMax());
                combineLists(combinedWindGustsMax, daily.windGustsMax());
                combineLists(combinedShortwaveRadiationSum, daily.shortwaveRadiationSum());
                combineLists(combinedSunshineDuration, daily.sunshineDuration());
            }
        }

        // Теперь рассчитываем почвенные параметры для КАЖДОГО дня
        List<Double> soilTemperature0cmMean = new ArrayList<>();
        List<Double> soilTemperature6cmMean = new ArrayList<>();
        List<Double> soilMoisture0to1cmMean = new ArrayList<>();

        for (String date : combinedTime) {
            logger.info("Calculating soil parameters for date: {}", date);

            List<Double> dailyTemp0cm = new ArrayList<>();
            List<Double> dailyTemp6cm = new ArrayList<>();
            List<Double> dailyMoisture = new ArrayList<>();

            // Собираем все почасовые данные за текущую дату
            for (Hourly hourly : hourlyList) {
                if (hourly != null && hourly.time() != null) {
                    for (int i = 0; i < hourly.time().size(); i++) {
                        String hourlyTime = hourly.time().get(i);
                        if (hourlyTime.startsWith(date)) {
                            if (hourly.soilTemperature0cm() != null && i < hourly.soilTemperature0cm().size()) {
                                dailyTemp0cm.add(hourly.soilTemperature0cm().get(i));
                            }
                            if (hourly.soilTemperature6cm() != null && i < hourly.soilTemperature6cm().size()) {
                                dailyTemp6cm.add(hourly.soilTemperature6cm().get(i));
                            }
                            if (hourly.soilMoisture0To1Cm() != null && i < hourly.soilMoisture0To1Cm().size()) {
                                dailyMoisture.add(hourly.soilMoisture0To1Cm().get(i));
                            }
                        }
                    }
                }
            }

            soilTemperature0cmMean.add(calculateAverage(dailyTemp0cm));
            soilTemperature6cmMean.add(calculateAverage(dailyTemp6cm));
            soilMoisture0to1cmMean.add(calculateAverage(dailyMoisture));

            logger.info("Soil params for {}: temp0cm={}, temp6cm={}, moisture={}",
                    date, calculateAverage(dailyTemp0cm), calculateAverage(dailyTemp6cm), calculateAverage(dailyMoisture));
        }

        return new Daily(
                combinedTime,
                combinedTemperatureMax,
                combinedTemperatureMin,
                combinedPrecipitationSum,
                combinedReferenceEvapotranspiration,
                combinedTemperatureMean,
                combinedRelativeHumidityMean,
                combinedRelativeHumidityMin,
                combinedWindSpeedMax,
                combinedWindGustsMax,
                combinedShortwaveRadiationSum,
                combinedSunshineDuration,
                soilTemperature0cmMean,
                soilTemperature6cmMean,
                soilMoisture0to1cmMean
        );
    }

    private double calculateAverage(List<Double> values) {
        if (values == null || values.isEmpty()) {
            return 0.0;
        }
        return values.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }

    private <T> void combineLists(List<T> target, List<T> source) {
        if (source != null) {
            target.addAll(source);
        }
    }
}