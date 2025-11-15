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

        CombinedHourlyData data = combineHourlyData(hourlyList);
        return buildHourly(data);
    }

    private CombinedHourlyData combineHourlyData(List<Hourly> hourlyList) {
        CombinedHourlyData data = new CombinedHourlyData();

        for (Hourly hourly : hourlyList) {
            if (hourly != null) {
                logger.info("Combining hourly data for time: {}", hourly.time());

                combineLists(data.time, hourly.time());
                combineLists(data.temperature, hourly.temperature());
                combineLists(data.relativeHumidity, hourly.relativeHumidity());
                combineLists(data.surfacePressure, hourly.surfacePressure());
                combineLists(data.precipitation, hourly.precipitation());
                combineLists(data.rain, hourly.rain());
                combineLists(data.snowfall, hourly.snowfall());
                combineLists(data.precipitationProbability, hourly.precipitationProbability());
                combineLists(data.dewPoint, hourly.dewPoint());
                combineLists(data.windGusts, hourly.windGusts());
                combineLists(data.windDirection, hourly.windDirection());
                combineLists(data.sunshineDuration, hourly.sunshineDuration());
                combineLists(data.windSpeed, hourly.windSpeed());
                combineLists(data.shortwaveRadiation, hourly.shortwaveRadiation());
                combineLists(data.uvIndex, hourly.uvIndex());
                combineLists(data.soilTemperature0cm, hourly.soilTemperature0cm());
                combineLists(data.soilTemperature6cm, hourly.soilTemperature6cm());
                combineLists(data.soilTemperature18cm, hourly.soilTemperature18cm());
                combineLists(data.soilTemperature54cm, hourly.soilTemperature54cm());
                combineLists(data.soilMoisture0To1Cm, hourly.soilMoisture0To1Cm());
                combineLists(data.soilMoisture1To3Cm, hourly.soilMoisture1To3Cm());
                combineLists(data.soilMoisture3To9Cm, hourly.soilMoisture3To9Cm());
                combineLists(data.soilMoisture9To27Cm, hourly.soilMoisture9To27Cm());
                combineLists(data.soilMoisture27To81Cm, hourly.soilMoisture27To81Cm());
            }
        }

        return data;
    }
    private Hourly buildHourly(CombinedHourlyData data) {
        return new Hourly(
                data.time,
                data.temperature,
                data.relativeHumidity,
                data.dewPoint,
                data.surfacePressure,
                data.precipitation,
                data.rain,
                data.snowfall,
                data.precipitationProbability,
                data.windSpeed,
                data.windGusts,
                data.windDirection,
                data.shortwaveRadiation,
                data.uvIndex,
                data.sunshineDuration,
                data.soilTemperature0cm,
                data.soilTemperature6cm,
                data.soilTemperature18cm,
                data.soilTemperature54cm,
                data.soilMoisture0To1Cm,
                data.soilMoisture1To3Cm,
                data.soilMoisture3To9Cm,
                data.soilMoisture9To27Cm,
                data.soilMoisture27To81Cm
        );
    }



    private <T> void combineLists(List<T> target, List<T> source) {
        if (source != null) {
            target.addAll(source);
        }
    }

    class CombinedHourlyData {
        List<String> time = new ArrayList<>();
        List<Double> temperature = new ArrayList<>();
        List<Double> relativeHumidity = new ArrayList<>();
        List<Double> surfacePressure = new ArrayList<>();
        List<Double> precipitation = new ArrayList<>();
        List<Double> rain = new ArrayList<>();
        List<Double> snowfall = new ArrayList<>();
        List<Double> precipitationProbability = new ArrayList<>();
        List<Double> dewPoint = new ArrayList<>();
        List<Double> windGusts = new ArrayList<>();
        List<Integer> windDirection = new ArrayList<>();
        List<Integer> sunshineDuration = new ArrayList<>();
        List<Double> windSpeed = new ArrayList<>();
        List<Double> shortwaveRadiation = new ArrayList<>();
        List<Double> uvIndex = new ArrayList<>();
        List<Double> soilTemperature0cm = new ArrayList<>();
        List<Double> soilTemperature6cm = new ArrayList<>();
        List<Double> soilTemperature18cm = new ArrayList<>();
        List<Double> soilTemperature54cm = new ArrayList<>();
        List<Double> soilMoisture0To1Cm = new ArrayList<>();
        List<Double> soilMoisture1To3Cm = new ArrayList<>();
        List<Double> soilMoisture3To9Cm = new ArrayList<>();
        List<Double> soilMoisture9To27Cm = new ArrayList<>();
        List<Double> soilMoisture27To81Cm = new ArrayList<>();
    }
}
