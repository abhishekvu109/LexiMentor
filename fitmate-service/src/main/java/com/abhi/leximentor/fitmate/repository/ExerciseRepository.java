package com.abhi.leximentor.fitmate.repository;

import com.abhi.leximentor.fitmate.entities.BodyPart;
import com.abhi.leximentor.fitmate.entities.Exercise;
import com.abhi.leximentor.fitmate.entities.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long>, JpaSpecificationExecutor<Exercise> {
    Stream<Exercise> findAllBy();

    Exercise findByRefId(long refId);

    Exercise findByName(String name);

    List<Exercise> findByRefIdIn(List<Long> refIds);

    List<Exercise> findByTargetBodyPart(BodyPart bodyPart);

    List<Exercise> findByTraining(Training training);

    List<Exercise> findByTrainingAndTargetBodyPart(Training training, BodyPart bodyPart);

    List<Exercise> findByTargetBodyPartRefId(long targetBodyPartRefId);
}
