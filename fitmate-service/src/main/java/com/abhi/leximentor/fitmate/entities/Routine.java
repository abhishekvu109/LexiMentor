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

    @Column(name = "is_completed")
    private boolean isCompleted;

    @Column(name = "completion_unit")
    private String completionUnit;

    @Column(name = "measurement")
    private int measurement;

    @CreationTimestamp
    @Column(name = "crtn_date")
    private LocalDateTime crtndate;

    @UpdateTimestamp
    @Column(name = "last_upd_date")
    private LocalDateTime lastUpdDate;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "excercise_id")
    private Exercise excercise;
}
