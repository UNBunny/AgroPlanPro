package com.omstu.agriculturefield.model.crop;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "disease_resistances")
@Data
public class DiseaseResistance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private Disease disease;

    @ManyToOne(fetch = FetchType.LAZY)
    private CropVariety cropVariety;

    private ResistanceLevel resistanceLevel;
}
