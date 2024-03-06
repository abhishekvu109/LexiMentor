package com.abhi.leximentor.inventory.repository.inv;

import com.abhi.leximentor.inventory.entities.inv.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
