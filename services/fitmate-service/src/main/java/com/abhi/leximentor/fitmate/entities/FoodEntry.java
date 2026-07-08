package com.abhi.leximentor.fitmate.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "fitmate_food_entry")
public class FoodEntry {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column(name = "uuid", unique = true)
    private String uuid;

    @Column(name = "ref_id", unique = true)
    private long refId;

    @Column(name = "username", nullable = false)
    private String username;

    @Column(name = "entry_date", nullable = false)
    private LocalDate entryDate;

    @Column(name = "entry_time")
    private LocalTime entryTime;

    @Column(name = "meal_type", nullable = false)
    private String mealType;

    @Column(name = "food_name", nullable = false)
    private String foodName;

    @Column(name = "serving_qty")
    private double servingQty;

    @Column(name = "serving_unit")
    private String servingUnit;

    @Column(name = "calories", nullable = false)
    private double calories;

    @Column(name = "protein")
    private double protein;

    @Column(name = "carbs")
    private double carbs;

    @Column(name = "fat")
    private double fat;

    @Column(name = "fiber")
    private double fiber;

    @Column(name = "sugar")
    private double sugar;

    @Column(name = "sodium")
    private double sodium;

    @Column(name = "source_type")
    private String sourceType;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "crtn_date")
    private LocalDateTime crtnDate;

    @UpdateTimestamp
    @Column(name = "last_upd_date")
    private LocalDateTime lastUpdDate;
}
