package com.omstu.weatherservice.dto;

import org.locationtech.jts.geom.Coordinate;

import java.util.List;

public record FieldWeatherRequest(List<Coordinate> polygon) {
}
