package com.abhi.leximentor.fitmate.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "fitmate_routine_drill")
public class Drill {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column(name = "uuid", unique = true)
    private String uuid;

    @Column(name = "ref_id", unique = true)
    private long refId;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne
    @JoinColumn(name = "exercise_id")
    private Exercise exercise;

    @Column(name = "routine_id")
    private long routine;

    @ManyToOne
    @JoinColumn(name = "routine_id2")
    private Routine routineObj;

//    @ManyToOne
//    @JoinColumn(name = "muscle_id")
//    private Muscle muscle;

    @Column(name = "measurement_unit")
    private String measurementUnit;

    @Column(name = "measurement")
    private double measurement;

    @Column(name = "unit")
    private String unit;

    @Column(name = "repetition")
    private int repetition;

    @Column(name = "burntCalories")
    private double burntCalories;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "crtn_date")
    private LocalDateTime crtnDate;

    @UpdateTimestamp
    @Column(name = "last_upd_date")
    private LocalDateTime lastUpdDate;
}
