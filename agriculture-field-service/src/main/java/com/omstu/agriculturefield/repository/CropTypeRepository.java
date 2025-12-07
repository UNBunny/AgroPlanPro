package com.omstu.agriculturefield.repository;

import com.omstu.agriculturefield.model.crop.CropType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CropTypeRepository extends JpaRepository<CropType, Long> {
}
