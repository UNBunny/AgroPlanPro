package com.omstu.agriculturefield.field.dto;

import java.util.List;

public record AgriculturalFieldRequest(
        String fieldName,
        String cropType,
        String status,
        List<List<Double>> coordinates,
        List<List<List<Double>>> holes
) {}
