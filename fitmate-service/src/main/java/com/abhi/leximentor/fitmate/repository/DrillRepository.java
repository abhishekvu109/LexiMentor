package com.abhi.leximentor.fitmate.repository;

import com.abhi.leximentor.fitmate.entities.Drill;
import com.abhi.leximentor.fitmate.entities.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DrillRepository extends JpaRepository<Drill, Long> {
    Drill findByRefId(long refId);

    List<Drill> findByRefIdIn(List<Long> refIds);

    List<Drill> findByExerciseOrderByCrtnDateDesc(Exercise exercise);

    List<Drill> findByRoutine(long routine);
}
