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
@ToString
@Entity
@Table(name = "fitmate_excercise")
public class Excercise {
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

    @Column(name = "unit", unique = true)
    private String unit;

    @Column(name = "status")
    private int status;

    @CreationTimestamp
    @Column(name = "crtn_date")
    private LocalDateTime crtnDate;

    @UpdateTimestamp
    @Column(name = "last_upd_date")
    private LocalDateTime lastUpdDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "training_metatdata_id")
    private TrainingMetadata trainingMetadata;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "target_body_part")
    private BodyParts targetBodyPart;

    @ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinTable(name = "fitmate_excercise_to_body_parts", joinColumns = {@JoinColumn(name = "excercise_id")}, inverseJoinColumns = {@JoinColumn(name = "body_part_id")})
    private List<BodyParts> secondaryBodyParts;

}
