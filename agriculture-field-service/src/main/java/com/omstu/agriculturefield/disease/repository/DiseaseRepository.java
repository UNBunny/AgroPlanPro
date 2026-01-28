package com.omstu.agriculturefield.disease.repository;

import com.omstu.agriculturefield.disease.model.Disease;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DiseaseRepository extends JpaRepository<Disease, Long> {
}
