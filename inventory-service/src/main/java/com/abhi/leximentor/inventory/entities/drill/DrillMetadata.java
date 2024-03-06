package com.abhi.leximentor.inventory.entities.drill;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "drill_metadata")
public class DrillMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "drill_id")
    private long id;

    @Column(name = "ref_id", unique = true, nullable = false)
    private String refId;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private int status;

    @CreationTimestamp
    @Column(name = "crtn_date")
    private LocalDateTime crtnDate;

    @UpdateTimestamp
    @Column(name = "last_upd_date")
    private LocalDateTime lastUpdDate;

    @Column(name = "overall_score")
    private double overallScore;

    @OneToMany(mappedBy = "drillId", cascade = CascadeType.ALL)
    private List<DrillSet> drillSetList;

    @OneToMany(mappedBy = "drillId", cascade = CascadeType.ALL)
    private List<DrillChallenge> drillChallenges;
}
