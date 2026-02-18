package com.abhi.leximentor.leximentor.repository.drill;

import com.abhi.leximentor.leximentor.entities.drill.ChallengeScores;
import com.abhi.leximentor.leximentor.entities.drill.ChallengeEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrillEvaluationRepository extends JpaRepository<ChallengeEvaluation, Long> {
    public List<ChallengeEvaluation> findByDrillChallengeScoresIn(List<ChallengeScores> challengeScores);
}
