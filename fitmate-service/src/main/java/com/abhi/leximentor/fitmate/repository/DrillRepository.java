package com.abhi.leximentor.fitmate.repository;

import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    Page<Drill> findByExerciseOrderByCrtnDateDesc(Exercise exercise, Pageable pageable);

    List<Drill> findByRoutine(long routine);

    /**
     * Loads a user's drill history for a specific exercise, newest first.
     * Used by the recommendation engine to apply progressive overload logic.
     *
     * <p>Uses an explicit JOIN and binds the exercise by its numeric PK
     * ({@code exerciseId}) rather than passing the full entity object.
     * Passing an entity with lazy-loaded collections (@OneToMany) as a JPQL
     * parameter can cause Hibernate to silently return empty results when the
     * entity is fetched in a different session or its proxy is not fully
     * initialised — using the raw id avoids that entirely.</p>
     */
    @Query("SELECT d FROM Drill d JOIN d.routineObj r WHERE r.username = :username AND d.exercise.id = :exerciseId ORDER BY r.routineDate DESC")
    List<Drill> findByUsernameAndExerciseOrderByRoutineDateDesc(@Param("username") String username,
                                                                @Param("exerciseId") long exerciseId);
}
