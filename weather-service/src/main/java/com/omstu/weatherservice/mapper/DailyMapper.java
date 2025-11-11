package com.omstu.weatherservice.mapper;

import com.omstu.weatherservice.dto.Daily;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface DailyMapper {

    DailyMapper INSTANCE = Mappers.getMapper(DailyMapper.class);

    default Daily combineDailyList(List<Daily> dailyList) {
        if (dailyList == null || dailyList.isEmpty()) {
            return null;
        }

        Daily result = new Daily(
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

        for (Daily daily : dailyList) {
            if (daily != null) {
                combineLists(result.time(), daily.time());
                combineLists(result.temperature_2m_max(), daily.temperature_2m_max());
                combineLists(result.temperature_2m_min(), daily.temperature_2m_min());
                combineLists(result.precipitation_sum(), daily.precipitation_sum());
                combineLists(result.et0_fao_evapotranspiration(), daily.et0_fao_evapotranspiration());
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