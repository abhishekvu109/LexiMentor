package com.abhi.leximentor.inventory.repository;

import com.abhi.leximentor.inventory.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
