package com.abhi.leximentor.leximentor.entities.drill;

import com.abhi.leximentor.leximentor.entities.NamedObject;
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
@ToString(exclude = {"drillSetList", "challenges"})
@Entity
@Table(name = "drill")
public class Drill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "`key`", unique = true, nullable = false)
    private String key;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private int status;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "overall_score")
    private double overallScore;

    @OneToMany(mappedBy = "drill", cascade = CascadeType.ALL)
    private List<DrillSet> drillSetList;

    @OneToMany(mappedBy = "drill", cascade = CascadeType.ALL)
    private List<Challenge> challenges;

    @Column(name = "drill_name")
    private String drillName;

    @OneToOne
    private NamedObject namedObject;
}

