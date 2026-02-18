package com.abhi.leximentor.leximentor.repository.drill;

import com.abhi.leximentor.leximentor.entities.drill.DrillMetadata;
import com.abhi.leximentor.leximentor.entities.drill.DrillSet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrillSetRepository extends JpaRepository<DrillSet, Long> {
    public DrillSet findByRefId(long refId);

    public List<DrillSet> findDrillSetByDrillId(DrillMetadata drillMetadata);
}
