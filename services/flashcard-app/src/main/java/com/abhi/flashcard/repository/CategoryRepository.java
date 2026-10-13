package com.abhi.flashcard.repository;

import com.abhi.flashcard.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByRefId(String refId);

    List<Category> findByParentIsNull();

    List<Category> findByParentRefId(String parentRefId);

    boolean existsByNameAndParentRefId(String name, String parentRefId);

    boolean existsByNameAndParentIsNull(String name);
}
