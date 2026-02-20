package com.omstu.agriculturefield.crop.repository;

import com.omstu.agriculturefield.crop.model.CropHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CropHistoryRepository extends JpaRepository<CropHistory, Long> {

    @Query("SELECT h FROM CropHistory h WHERE h.field.id = :fieldId ORDER BY h.plantingDate DESC")
    List<CropHistory> findByFieldIdOrderByPlantingDateDesc(@Param("fieldId") Long fieldId);
}
