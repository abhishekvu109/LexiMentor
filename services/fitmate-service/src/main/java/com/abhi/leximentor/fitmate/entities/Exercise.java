package com.abhi.leximentor.fitmate.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
@ToString(exclude = {"training", "targetBodyPart"})
@Entity
@Table(name = "fitmate_exercise")
public class Exercise {
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column(name = "uuid", unique = true)
    private String uuid;

    @Column(name = "ref_id", unique = true)
    private long refId;

    @Column(name = "name", unique = true)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "unit")
    private String unit;

    @Column(name = "status")
    private int status;

    @CreationTimestamp
    @Column(name = "crtn_date")
    private LocalDateTime crtnDate;

    @UpdateTimestamp
    @Column(name = "last_upd_date")
    private LocalDateTime lastUpdDate;

    @ManyToOne
    @JoinColumn(name = "training_metatdata_id")
    private Training training;

    @ManyToOne
    @JoinColumn(name = "target_body_part")
    private BodyPart targetBodyPart;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinTable(name = "fitmate_exercise_muscle", joinColumns = @JoinColumn(name = "exercise_id"), inverseJoinColumns = @JoinColumn(name = "muscle_id"))
    private List<Muscle> targetMuscles;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "fitmate_exercise_resources", joinColumns = @JoinColumn(name = "exercise_id"), inverseJoinColumns = @JoinColumn(name = "resource_id"))
    private List<FitmateResource> resources;

    @ElementCollection
    @CollectionTable(
            name = "fitmate_exercise_equipments",
            joinColumns = @JoinColumn(name = "exercise_id")
    )
    @Column(name = "equipment")
    private List<String> equipments;

    @Column(name = "difficulty_level")
    private int difficultyLevel;

    @Column(name = "risk_level")
    private int riskLevel;
}
