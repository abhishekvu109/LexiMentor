package com.abhi.leximentor.inventory.repository.drill;

import com.abhi.leximentor.inventory.entities.drill.DrillChallengeScores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrillChallengeScoreRepository extends JpaRepository<DrillChallengeScores, Long> {
    List<DrillChallengeScores> findByChallengeId(long challengeId);
}
