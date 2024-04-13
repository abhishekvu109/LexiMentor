package com.abhi.leximentor.fitmate.repository;

import com.abhi.leximentor.fitmate.entities.TrainingMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingMetadataRepository extends JpaRepository<TrainingMetadata, Long> {
    TrainingMetadata findByRefId(long refId);

    TrainingMetadata findByName(String name);

    List<TrainingMetadata> findByRefIdIn(List<Long> refIds);

}
