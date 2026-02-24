package com.abhi.leximentor.leximentor.repository.inv;

import com.abhi.leximentor.leximentor.constants.ChallengeType;
import com.abhi.leximentor.leximentor.entities.inv.Evaluator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvaluatorRepository extends JpaRepository<Evaluator, Long> {
    Evaluator findByName(String name);

    Evaluator findByNameAndChallengeType(String name, ChallengeType challengeType);

    Evaluator findByKey(String key);

    List<Evaluator> findByChallengeType(ChallengeType challengeType);

    List<Evaluator> findByChallengeTypeIgnoreCase(ChallengeType challengeType);
}
