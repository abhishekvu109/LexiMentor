package com.abhi.leximentor.leximentor.repository.drill;

import com.abhi.leximentor.leximentor.constants.QueryConstants;
import com.abhi.leximentor.leximentor.entities.drill.Challenge;
import com.abhi.leximentor.leximentor.entities.drill.Drill;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Long>, JpaSpecificationExecutor<Challenge> {
    Challenge findByKey(String key);

    List<Challenge> findByDrill(Drill drill);

    @Query(value = QueryConstants.Analytics.Challenge.GET_CHALLENGE_METADATA)
    List<Tuple> findDrillAnalyticsGroupedByType();

    @Query(value = QueryConstants.Analytics.Challenge.GET_DRILL_TYPE_PERFORMANCE)
    List<Tuple> findDrillTypePerformance();

    @Query(value = QueryConstants.Analytics.Challenge.GET_DRILL_TRENDS, nativeQuery = true)
    List<Object[]> findDrillTrends(@Param("fromDate") LocalDate fromDate, @Param("username") String username);

    @Query(value = QueryConstants.Analytics.Challenge.GET_USER_DRILL_TYPE_PERFORMANCE)
    List<Tuple> findUserDrillTypePerformance(@Param("username") String username);

    List<Challenge> findByDrillAndUsernameIgnoreCase(Drill drill, String username);
}
