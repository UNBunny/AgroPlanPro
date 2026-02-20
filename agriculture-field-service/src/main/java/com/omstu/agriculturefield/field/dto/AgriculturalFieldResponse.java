package com.omstu.agriculturefield.field.dto;

import java.util.List;

public record AgriculturalFieldResponse(
        Long id,
        String fieldName,
        String cropType,
        String status,
        List<List<Double>> coordinates,
        List<List<List<Double>>> holes,
        Double areaHectares
) {}
