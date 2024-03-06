package com.abhi.leximentor.inventory.repository.drill;

import com.abhi.leximentor.inventory.entities.drill.DrillChallenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrillChallengeRepository extends JpaRepository<DrillChallenge, Long> {
}
