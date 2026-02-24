package com.abhi.leximentor.leximentor.repository.drill;

import com.abhi.leximentor.leximentor.entities.drill.ChallengeEvaluation;
import com.abhi.leximentor.leximentor.entities.drill.ChallengeScores;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChallengeEvaluationRepository extends JpaRepository<ChallengeEvaluation, Long> {
    List<ChallengeEvaluation> findByChallengeScoresIn(List<ChallengeScores> challengeScores);
}
