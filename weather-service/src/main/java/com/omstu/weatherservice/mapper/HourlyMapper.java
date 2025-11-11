package com.omstu.weatherservice.mapper;

import com.omstu.weatherservice.dto.Hourly;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface HourlyMapper {

    HourlyMapper INSTANCE = Mappers.getMapper(HourlyMapper.class);

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
                combineLists(result.time(), hourly.time());
                combineLists(result.temperature_2m(), hourly.temperature_2m());
                combineLists(result.relative_humidity_2m(), hourly.relative_humidity_2m());
                combineLists(result.surface_pressure(), hourly.surface_pressure());
                combineLists(result.precipitation(), hourly.precipitation());
                combineLists(result.rain(), hourly.rain());
                combineLists(result.snowfall(), hourly.snowfall());
                combineLists(result.wind_speed_10m(), hourly.wind_speed_10m());
                combineLists(result.shortwave_radiation(), hourly.shortwave_radiation());
                combineLists(result.uv_index(), hourly.uv_index());
                combineLists(result.soil_temperature_0cm(), hourly.soil_temperature_0cm());
                combineLists(result.soil_temperature_6cm(), hourly.soil_temperature_6cm());
                combineLists(result.soil_temperature_18cm(), hourly.soil_temperature_18cm());
                combineLists(result.soil_temperature_54cm(), hourly.soil_temperature_54cm());
                combineLists(result.soil_moisture_0_to_1cm(), hourly.soil_moisture_0_to_1cm());
                combineLists(result.soil_moisture_1_to_3cm(), hourly.soil_moisture_1_to_3cm());
                combineLists(result.soil_moisture_3_to_9cm(), hourly.soil_moisture_3_to_9cm());
                combineLists(result.soil_moisture_9_to_27cm(), hourly.soil_moisture_9_to_27cm());
                combineLists(result.soil_moisture_27_to_81cm(), hourly.soil_moisture_27_to_81cm());
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