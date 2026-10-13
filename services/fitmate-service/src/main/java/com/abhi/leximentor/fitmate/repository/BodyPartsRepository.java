package com.abhi.leximentor.fitmate.repository;

import com.abhi.leximentor.fitmate.entities.BodyPart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BodyPartsRepository extends JpaRepository<BodyPart, Long> {
    BodyPart findByRefId(long refId);

    BodyPart findByNameIgnoreCase(String name);
    BodyPart findByName(String name);

    List<BodyPart> findByRefIdIn(List<Long> refIds);

    List<BodyPart> findByNameIn(List<String> names);
}
