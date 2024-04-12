package com.abhi.leximentor.fitmate.repository;

import com.abhi.leximentor.fitmate.entities.BodyParts;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BodyPartsRepository extends JpaRepository<BodyParts, Long> {
    BodyParts findByRefId(long refId);

    BodyParts findByName(String name);

    List<BodyParts> findByRefIdIn(List<Long> refIds);

    List<BodyParts> findByNameIn(List<String> names);
}
