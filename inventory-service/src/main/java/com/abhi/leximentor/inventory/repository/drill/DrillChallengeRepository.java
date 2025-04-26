package com.abhi.leximentor.inventory.repository.drill;

import com.abhi.leximentor.inventory.constants.QueryConstants;
import com.abhi.leximentor.inventory.dto.drill.DrillChallengeDTO;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.entities.drill.DrillMetadata;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrillChallengeRepository extends JpaRepository<DrillChallenge, Long> {
    public DrillChallenge findByRefId(long refId);

    public List<DrillChallenge> findByDrillId(DrillMetadata drillMetadata);

    public List<DrillChallenge> findTop10ByDrillTypeOrderByDrillScoreDesc(String drillType);

    public List<DrillChallenge> findTop10ByDrillTypeOrderByDrillScoreAsc(String drillType);

    @Query(value = QueryConstants.Analytics.DrillChallenge.GET_DRILL_CHALLENGE_METADATA)
    List<Tuple> findDrillAnalyticsGroupedByType();

}
