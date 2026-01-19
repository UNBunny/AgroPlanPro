package com.omstu.agriculturefield.repository;

import com.omstu.agriculturefield.model.crop.CropVariety;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CropVarietyRepository extends JpaRepository<CropVariety, Long> {
}
