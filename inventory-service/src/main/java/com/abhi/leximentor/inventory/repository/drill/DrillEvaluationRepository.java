package com.abhi.leximentor.inventory.repository.drill;

import com.abhi.leximentor.inventory.entities.drill.DrillChallengeScores;
import com.abhi.leximentor.inventory.entities.drill.DrillEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrillEvaluationRepository extends JpaRepository<DrillEvaluation, Long> {
    public List<DrillEvaluation> findByDrillChallengeScoresIn(List<DrillChallengeScores> drillChallengeScores);
}
