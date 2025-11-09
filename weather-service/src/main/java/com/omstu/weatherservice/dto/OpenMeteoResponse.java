package com.omstu.weatherservice.dto;

public record OpenMeteoResponse(Double latitude, Double longitude, Double elevation,
                                Hourly hourly, Daily daily) {

}
