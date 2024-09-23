package com.lgusar.rssfeedanalyzer2.repositories;

import com.lgusar.rssfeedanalyzer2.entities.AnalysisResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnalysisResultRepository extends JpaRepository<AnalysisResult, Long> {
}
