package com.abhi.leximentor.fitmate.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "fitmate_body_parts")
public class BodyParts {
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

    @Column(name = "status")
    private int status;

    @ManyToMany(mappedBy = "secondaryBodyParts",fetch = FetchType.LAZY)
    private List<Excercise> excercises;

    @OneToOne(mappedBy = "targetBodyPart")
    private Excercise excercise;
}
