package com.omstu.agroplanpro.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;
import org.locationtech.jts.geom.MultiPolygon;
import org.locationtech.jts.geom.Polygon;


@Entity
@Table(name = "agricultural_fields")
@Data
@ToString
public class AgriculturalField {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fieldName;

    private String crop_type;

    private String status;

    @Column(columnDefinition = "geometry(Polygon, 4326)")
    private Polygon geom;

    @Column(columnDefinition = "geometry(MultiPolygon, 4326)")
    private MultiPolygon holes;

    @Column(name = "area_hectares", columnDefinition = "NUMERIC(12,2)")
    private Double areaHectares;
}
