package com.exercise.rssfeedanalyzer.repository;

import com.exercise.rssfeedanalyzer.model.entity.TopicEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnalyzerRepository extends JpaRepository<TopicEntity, Long> {
    List<TopicEntity> findByUniqueIdentifier(String uniqueIdentifier);
}
