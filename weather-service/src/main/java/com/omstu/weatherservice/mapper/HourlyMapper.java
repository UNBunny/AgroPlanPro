package com.omstu.weatherservice.mapper;

import com.omstu.weatherservice.dto.Hourly;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface HourlyMapper {

    HourlyMapper INSTANCE = Mappers.getMapper(HourlyMapper.class);
    Logger logger = LoggerFactory.getLogger(HourlyMapper.class);


    default Hourly combineHourlyList(List<Hourly> hourlyList) {
        if (hourlyList == null || hourlyList.isEmpty()) {
            return null;
        }

        Hourly result = new Hourly(
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>(),
                new ArrayList<>()
        );

        for (Hourly hourly : hourlyList) {
            if (hourly != null) {
                logger.info("Hourly time: {}", hourly.time());
                combineLists(result.time(), hourly.time());
                combineLists(result.temperature(), hourly.temperature());
                combineLists(result.relativeHumidity(), hourly.relativeHumidity());
                combineLists(result.surfacePressure(), hourly.surfacePressure());
                combineLists(result.precipitation(), hourly.precipitation());
                combineLists(result.rain(), hourly.rain());
                combineLists(result.snowfall(), hourly.snowfall());
                combineLists(result.precipitationProbability(), hourly.precipitationProbability());
                combineLists(result.dewPoint(), hourly.dewPoint());
                combineLists(result.windGusts(), hourly.windGusts());
                combineLists(result.windDirection(), hourly.windDirection());
                combineLists(result.sunshineDuration(), hourly.sunshineDuration());
                combineLists(result.windSpeed(), hourly.windSpeed());
                combineLists(result.shortwaveRadiation(), hourly.shortwaveRadiation());
                combineLists(result.uvIndex(), hourly.uvIndex());
                combineLists(result.soilTemperature0cm(), hourly.soilTemperature0cm());
                combineLists(result.soilTemperature6cm(), hourly.soilTemperature6cm());
                combineLists(result.soilTemperature18cm(), hourly.soilTemperature18cm());
                combineLists(result.soilTemperature54cm(), hourly.soilTemperature54cm());
                combineLists(result.soilMoisture0To1Cm(), hourly.soilMoisture0To1Cm());
                combineLists(result.soilMoisture1To3Cm(), hourly.soilMoisture1To3Cm());
                combineLists(result.soilMoisture3To9Cm(), hourly.soilMoisture3To9Cm());
                combineLists(result.soilMoisture9To27Cm(), hourly.soilMoisture9To27Cm());
                combineLists(result.soilMoisture27To81Cm(), hourly.soilMoisture27To81Cm());
            }
        }

        return result;
    }

    private <T> void combineLists(List<T> target, List<T> source) {
        if (source != null) {
            target.addAll(source);
        }
    }
}