package com.omstu.agriculturefield.disease.model;

import com.omstu.agriculturefield.crop.model.CropType;
import com.omstu.agriculturefield.disease.model.enums.RiskLevel;
import com.omstu.agriculturefield.disease.model.enums.DiseaseType;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.Data;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "diseases")
@Data
public class Disease {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String scientificName;

    private String commonName;

    @Enumerated(EnumType.STRING)
    private DiseaseType diseaseType; // Грибковое, бактериальное и тд

    @ManyToMany
    @JoinTable(
            name = "disease_affected_crops",
            joinColumns = @JoinColumn(name = "disease_id"),
            inverseJoinColumns = @JoinColumn(name = "crop_type_id")
    )
    private Set<CropType> affectedCrops = new HashSet<>(); // Какие культуры поражает

    private String symptoms;

    private String preventionMeasures; // Меры профилактики

    private String treatmentMethods; // Методы лечения

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    private String activeSeason;

    private String favorableConditions; // Благоприятные условия для развития

    private String imageUrl;

    private Boolean isActive = true;
}
