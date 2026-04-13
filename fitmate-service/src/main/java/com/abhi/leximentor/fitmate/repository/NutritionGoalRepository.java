package com.abhi.leximentor.fitmate.repository;

import com.abhi.leximentor.fitmate.entities.NutritionGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NutritionGoalRepository extends JpaRepository<NutritionGoal, Long> {
    NutritionGoal findTopByUsernameAndEffectiveToIsNullOrderByEffectiveFromDesc(String username);
}
