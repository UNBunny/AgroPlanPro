package com.omstu.agriculturefield.crop.repository;

import com.omstu.agriculturefield.crop.model.CropHistory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CropHistoryRepository extends JpaRepository<CropHistory, Long> {
}
