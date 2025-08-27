package com.abhi.leximentor.fitmate.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
@ToString(exclude = {"excercises", "targetMuscles"})
@Entity
@Table(name = "fitmate_body_parts")
public class BodyPart {
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

    @Column(name = "status")
    private int status;

    @OneToMany(mappedBy = "targetBodyPart")
    private List<Exercise> exercises;

    @OneToMany(mappedBy = "bodyPart")
    private List<Muscle> targetMuscles;


    @OneToMany
    @JoinTable(name = "fitmate_bodypart_resources", joinColumns = @JoinColumn(name = "bodypart_id"), inverseJoinColumns = @JoinColumn(name = "resource_id"))
    private List<FitmateResource> resources;
}
