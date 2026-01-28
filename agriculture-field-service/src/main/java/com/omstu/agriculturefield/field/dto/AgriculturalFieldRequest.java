package com.omstu.agriculturefield.field.dto;

import java.util.List;

public record AgriculturalFieldRequest(
        String fieldName,
        String crop_type,
        String status,
        List<List<Double>> coordinates,
        List<List<List<Double>>> holes,
        Double areaHectares) {
}