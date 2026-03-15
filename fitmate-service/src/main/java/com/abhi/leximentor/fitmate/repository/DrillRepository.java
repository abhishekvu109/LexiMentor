package com.abhi.leximentor.fitmate.repository;

import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrillRepository extends JpaRepository<Drill, Long> {
    Drill findByRefId(long refId);

    List<Drill> findByRefIdIn(List<Long> refIds);

    List<Drill> findByExerciseOrderByCrtnDateDesc(Exercise exercise);

    List<Drill> findByRoutine(long routine);

    /**
     * Loads a user's drill history for a specific exercise, newest first.
     * Used by the recommendation engine to apply progressive overload logic.
     * Joins through {@code routineObj} (the JPA relation on {@code Drill}) to
     * filter by username and sort by the routine's workout date.
     */
    @Query("SELECT d FROM Drill d WHERE d.routineObj.username = :username AND d.exercise = :exercise ORDER BY d.routineObj.routineDate DESC")
    List<Drill> findByUsernameAndExerciseOrderByRoutineDateDesc(@Param("username") String username,
                                                                @Param("exercise") Exercise exercise);
}
