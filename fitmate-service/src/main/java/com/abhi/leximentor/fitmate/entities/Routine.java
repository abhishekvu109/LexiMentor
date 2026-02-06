package com.abhi.leximentor.fitmate.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
@ToString(exclude = {"training"})
@Entity
@Table(name = "fitmate_routine")
public class Routine {
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

    @Column(name = "status")
    private int status;

    @CreationTimestamp
    @Column(name = "crtn_date")
    private LocalDateTime crtnDate;

    @UpdateTimestamp
    @Column(name = "last_upd_date")
    private LocalDateTime lastUpdDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "training_id")
    private Training training;

    @OneToMany(mappedBy = "routineObj", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Drill> drills;

    @Column(name = "routine_date")
    private LocalDate routineDate;

    @Column(name = "burnt_calories")
    private double burntCalories;

    @Column(name = "duration_in_minutes")
    private double durationInMinutes;

    @Column(name = "username")
    private String username;
}
