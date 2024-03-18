package com.abhi.leximentor.inventory.entities.inv;

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
@Table(name = "inv_evaluator")
public class Evaluator {
    @Id
    @Column(name = "evaluator_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column(name = "ref_id", nullable = false, unique = true)
    private long refId;

    @Column(name = "uuid", nullable = false, unique = true)
    private String uuid;

    @Column(name = "name")
    private String name;

    @CreationTimestamp
    @Column(name = "crtn_date")
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime crtnDate;

    @UpdateTimestamp
    @Column(name = "last_upd_date")
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime lastUpdDate;

    @Column(name = "status")
    private int status;

    @Column(name = "drill_type")
    private String drillType;
}
