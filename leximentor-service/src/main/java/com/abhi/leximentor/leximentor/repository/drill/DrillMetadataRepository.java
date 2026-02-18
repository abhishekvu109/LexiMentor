package com.abhi.leximentor.leximentor.repository.drill;

import com.abhi.leximentor.leximentor.entities.drill.DrillMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrillMetadataRepository extends JpaRepository<DrillMetadata, Long> {
    public DrillMetadata findByRefId(long refId);
}
