package com.omstu.agriculturefield.rotation.model;

import com.omstu.agriculturefield.crop.model.CropType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "crop_rotation_rules")
@Data
public class CropRotationRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "predecessor_crop_id", nullable = false)
    private CropType predecessorCrop;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "successor_crop_id", nullable = false)
    private CropType successorCrop;

    private Boolean allowed;

    private Integer minGapYears;

    private String reason;
}
