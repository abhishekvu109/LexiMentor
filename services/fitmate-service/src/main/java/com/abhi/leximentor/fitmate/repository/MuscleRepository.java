package com.abhi.leximentor.fitmate.repository;

import com.abhi.leximentor.fitmate.entities.BodyPart;
import com.abhi.leximentor.fitmate.entities.Muscle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MuscleRepository extends JpaRepository<Muscle, Long> {
    Muscle findByRefId(long refId);

    List<Muscle> findByRefIdIn(List<Long> refIds);

    Muscle findByName(String name);

    List<Muscle> findByBodyPart(BodyPart bodyPart);

    @Query("SELECT m FROM Muscle m WHERE LOWER(m.name) IN :names")
    List<Muscle> findByNameInIgnoreCase(@Param("names") List<String> muscles);
}
