package com.abhi.leximentor.inventory.repository.drill;

import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import com.abhi.leximentor.inventory.entities.drill.DrillChallengeScores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrillChallengeScoreRepository extends JpaRepository<DrillChallengeScores, Long> {
    public List<DrillChallengeScores> findByChallengeId(DrillChallenge challengeId);

    public DrillChallengeScores findByRefId(long refId);
}
