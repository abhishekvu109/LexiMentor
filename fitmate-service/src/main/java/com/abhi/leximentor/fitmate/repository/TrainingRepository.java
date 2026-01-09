package com.abhi.leximentor.fitmate.repository;

import com.abhi.leximentor.fitmate.constants.QueryConstants;
import com.abhi.leximentor.fitmate.entities.Training;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainingRepository extends JpaRepository<Training, Long> {
    Training findByRefId(long refId);

    Training findByName(String name);

    @Query(value = QueryConstants.Training.GET_TRAINING_BY_NAME, nativeQuery = true)
    Training findByNameIgnoreCase(String name);

    List<Training> findByRefIdIn(List<Long> refIds);

}
