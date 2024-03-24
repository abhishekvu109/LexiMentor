package com.abhi.leximentor.inventory.repository.drill;

import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.entities.drill.DrillMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrillChallengeRepository extends JpaRepository<DrillChallenge, Long> {
    public DrillChallenge findByRefId(long refId);

    public List<DrillChallenge> findByDrillId(DrillMetadata drillMetadata);
}
