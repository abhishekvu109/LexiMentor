package com.abhi.leximentor.fitmate.repository;

import com.abhi.leximentor.fitmate.constants.QueryConstants;
import com.abhi.leximentor.fitmate.entities.BodyParts;
import com.abhi.leximentor.fitmate.entities.Exercise;
import com.abhi.leximentor.fitmate.entities.TrainingMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {
    Exercise findByRefId(long refId);

    @Query(name = QueryConstants.ExerciseQueries.GET_EXERCISES_BY_NAME, nativeQuery = true)
    Exercise findByName(String name);

    List<Exercise> findByRefIdIn(List<Long> refIds);

    List<Exercise> findByTargetBodyPart(BodyParts bodyParts);

    List<Exercise> findByTrainingMetadata(TrainingMetadata trainingMetadata);
    List<Exercise> findByTrainingMetadataAndTargetBodyPart(TrainingMetadata trainingMetadata,BodyParts bodyParts);
}
