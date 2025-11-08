package com.omstu.agriculturefield.mapper;

import com.omstu.agriculturefield.dto.AgriculturalFieldRequest;
import com.omstu.agriculturefield.dto.AgriculturalFieldResponse;
import com.omstu.agriculturefield.model.AgriculturalField;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring")
public interface AgriculturalFieldMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "geom", source = "coordinates", qualifiedByName = "listToPolygon")
    @Mapping(target = "holes", source = "holes", qualifiedByName = "listToMultiPolygon")
    AgriculturalField toEntity(AgriculturalFieldRequest request);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "geom", source = "request.coordinates", qualifiedByName = "listToPolygon")
    @Mapping(target = "holes", source = "request.holes", qualifiedByName = "listToMultiPolygon")
    AgriculturalField toEntityWithId(Long id, AgriculturalFieldRequest request);

    @Mapping(target = "coordinates", source = "geom", qualifiedByName = "polygonToList")
    @Mapping(target = "holes", source = "holes", qualifiedByName = "multiPolygonToList")
    AgriculturalFieldResponse toResponse(AgriculturalField field);

    @Named("listToPolygon")
    default Polygon listToPolygon(List<List<Double>> coordinates) {
        if (coordinates == null || coordinates.isEmpty()) {
            return null;
        }

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Coordinate[] coords = new Coordinate[coordinates.size()];

        for (int i = 0; i < coordinates.size(); i++) {
            List<Double> point = coordinates.get(i);
            // Правильный порядок координат: долгота (x), широта (y)
            coords[i] = new Coordinate(point.get(0), point.get(1));
        }

        // Проверяем, замкнут ли полигон (первая и последняя точки должны совпадать)
        if (coords.length > 1 && !coords[0].equals2D(coords[coords.length - 1])) {
            // Создаем новый массив с дополнительной точкой
            Coordinate[] closedCoords = new Coordinate[coords.length + 1];
            System.arraycopy(coords, 0, closedCoords, 0, coords.length);
            closedCoords[coords.length] = new Coordinate(coords[0].x, coords[0].y);
            coords = closedCoords;
        }

        LinearRing ring = geometryFactory.createLinearRing(coords);
        return geometryFactory.createPolygon(ring, null);
    }

    @Named("listToMultiPolygon")
    default MultiPolygon listToMultiPolygon(List<List<List<Double>>> polygons) {
        if (polygons == null || polygons.isEmpty()) {
            return null;
        }

        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
        Polygon[] polygonArray = new Polygon[polygons.size()];

        for (int i = 0; i < polygons.size(); i++) {
            List<List<Double>> polygonCoords = polygons.get(i);
            Coordinate[] coords = new Coordinate[polygonCoords.size()];

            for (int j = 0; j < polygonCoords.size(); j++) {
                List<Double> point = polygonCoords.get(j);
                coords[j] = new Coordinate(point.get(0), point.get(1));
            }

            // Проверяем, замкнут ли полигон (первая и последняя точки должны совпадать)
            if (coords.length > 1 && !coords[0].equals2D(coords[coords.length - 1])) {
                // Создаем новый массив с дополнительной точкой
                Coordinate[] closedCoords = new Coordinate[coords.length + 1];
                System.arraycopy(coords, 0, closedCoords, 0, coords.length);
                closedCoords[coords.length] = new Coordinate(coords[0].x, coords[0].y);
                coords = closedCoords;
            }

            LinearRing ring = geometryFactory.createLinearRing(coords);
            polygonArray[i] = geometryFactory.createPolygon(ring, null);
        }

        return geometryFactory.createMultiPolygon(polygonArray);
    }


    @Named("polygonToList")
    default List<List<Double>> polygonToList(Polygon polygon) {
        if (polygon == null) {
            return null;
        }

        List<List<Double>> result = new ArrayList<>();
        Coordinate[] coordinates = polygon.getExteriorRing().getCoordinates();

        for (Coordinate coord : coordinates) {
            List<Double> point = new ArrayList<>();
            point.add(coord.x);
            point.add(coord.y);
            result.add(point);
        }

        return result;
    }


    @Named("multiPolygonToList")
    default List<List<List<Double>>> multiPolygonToList(MultiPolygon multiPolygon) {
        if (multiPolygon == null) {
            return null;
        }

        List<List<List<Double>>> result = new ArrayList<>();
        for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
            Polygon polygon = (Polygon) multiPolygon.getGeometryN(i);
            List<List<Double>> polygonCoords = new ArrayList<>();

            Coordinate[] coordinates = polygon.getExteriorRing().getCoordinates();
            for (Coordinate coord : coordinates) {
                List<Double> point = new ArrayList<>();
                point.add(coord.x);
                point.add(coord.y);
                polygonCoords.add(point);
            }

            result.add(polygonCoords);
        }

        return result;
    }
}
