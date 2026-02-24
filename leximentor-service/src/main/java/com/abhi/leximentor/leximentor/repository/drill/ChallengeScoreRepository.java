package com.abhi.leximentor.leximentor.repository.drill;

import com.abhi.leximentor.leximentor.entities.drill.Challenge;
import com.abhi.leximentor.leximentor.entities.drill.ChallengeScores;
import com.abhi.leximentor.leximentor.entities.drill.DrillSet;
import com.abhi.leximentor.leximentor.constants.QueryConstants;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeScoreRepository extends JpaRepository<ChallengeScores, Long> {
    List<ChallengeScores> findByChallenge(Challenge challenge);
    ChallengeScores findByKey(String key);
    ChallengeScores findByDrillSetAndChallenge(DrillSet drillSet, Challenge challenge);
    @Query(value = QueryConstants.Analytics.WordDifficulty.GET_WORD_DIFFICULTY_HEATMAP, nativeQuery = true)
    List<Object[]> findWordDifficultyHeatmap(@Param("limit") int limit);
}
