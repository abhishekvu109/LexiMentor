package com.abhi.leximentor.fitmate.repository;

import com.abhi.leximentor.fitmate.entities.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Exercise findByRefId(long refId);

    Exercise findByName(String name);

    List<Exercise> findByRefIdIn(List<Long> refIds);
}
