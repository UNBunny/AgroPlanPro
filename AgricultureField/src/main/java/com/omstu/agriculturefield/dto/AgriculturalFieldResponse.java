package com.omstu.agriculturefield.dto;

import java.util.List;

public record AgriculturalFieldResponse(
        Long id,
        String fieldName,
        String crop_type,
        String status,
        List<List<Double>> coordinates,
        List<List<List<Double>>> holes,
        Double areaHectares) {
}
