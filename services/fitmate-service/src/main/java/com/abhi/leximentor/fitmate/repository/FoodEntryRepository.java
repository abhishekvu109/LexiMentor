package com.abhi.leximentor.fitmate.repository;

import com.abhi.leximentor.fitmate.entities.FoodEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FoodEntryRepository extends JpaRepository<FoodEntry, Long> {
    Optional<FoodEntry> findByRefIdAndUsername(long refId, String username);

    List<FoodEntry> findByUsernameAndEntryDateBetweenOrderByEntryDateAscEntryTimeAsc(String username, LocalDate fromDate, LocalDate toDate);

    List<FoodEntry> findByUsernameAndEntryDateAndMealTypeOrderByEntryTimeAsc(String username, LocalDate entryDate, String mealType);

    List<FoodEntry> findByUsernameAndEntryDateOrderByEntryTimeAsc(String username, LocalDate entryDate);
}
