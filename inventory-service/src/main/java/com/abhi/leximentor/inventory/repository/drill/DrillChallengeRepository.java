package com.abhi.leximentor.inventory.repository.drill;

import com.abhi.leximentor.inventory.constants.QueryConstants;
import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.entities.drill.DrillMetadata;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DrillChallengeRepository extends JpaRepository<DrillChallenge, Long> {
    public DrillChallenge findByRefId(long refId);

    public List<DrillChallenge> findByDrillId(DrillMetadata drillMetadata);

    public List<DrillChallenge> findTop10ByDrillTypeOrderByDrillScoreDesc(String drillType);

    public List<DrillChallenge> findTop10ByDrillTypeOrderByDrillScoreAsc(String drillType);

    @Query(value = QueryConstants.Analytics.DrillChallenge.GET_DRILL_CHALLENGE_METADATA)
    List<Tuple> findDrillAnalyticsGroupedByType();

    @Query(value = QueryConstants.Analytics.DrillChallenge.GET_DRILL_TYPE_PERFORMANCE)
    List<Tuple> findDrillTypePerformance();

    @Query(value = QueryConstants.Analytics.DrillChallenge.GET_DRILL_TRENDS, nativeQuery = true)
    List<Object[]> findDrillTrends(@Param("fromDate") LocalDate fromDate, @Param("username") String username);

    @Query(value = QueryConstants.Analytics.DrillChallenge.GET_USER_DRILL_TYPE_PERFORMANCE)
    List<Tuple> findUserDrillTypePerformance(@Param("username") String username);

    List<DrillChallenge> findByDrillIdAndUsernameIgnoreCase(DrillMetadata drillMetadata, String username);
}
