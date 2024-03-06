package com.abhi.leximentor.inventory.repository.drill;

import com.abhi.leximentor.inventory.entities.drill.DrillMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrillMetadataRepository extends JpaRepository<DrillMetadata, Long> {
}
