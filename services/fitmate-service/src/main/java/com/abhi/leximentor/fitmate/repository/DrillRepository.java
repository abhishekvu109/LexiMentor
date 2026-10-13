package com.abhi.leximentor.fitmate.repository;

import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Exercise;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    @Modifying
    @Query(value = "UPDATE fitmate_routine_drill SET exercise_id = NULL WHERE exercise_id IN :ids", nativeQuery = true)
    void nullifyExerciseRefs(@Param("ids") List<Long> ids);
}
