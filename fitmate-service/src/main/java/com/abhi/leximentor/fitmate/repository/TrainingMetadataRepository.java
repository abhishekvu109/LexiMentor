package com.abhi.leximentor.fitmate.repository;

import com.abhi.leximentor.fitmate.entities.TrainingMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainingMetadataRepository extends JpaRepository<TrainingMetadata, Long> {
}
