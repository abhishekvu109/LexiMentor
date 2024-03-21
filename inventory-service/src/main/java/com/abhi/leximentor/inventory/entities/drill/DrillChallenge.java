package com.abhi.leximentor.inventory.entities.drill;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@Table(name = "drill_challenge")
public class DrillChallenge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "drill_challenge_id")
    private long id;

    @Column(name = "ref_id")
    private long refId;

    @Column(name = "uuid")
    private String uuid;

    @ManyToOne
    @JoinColumn(name = "drill_metadata_id")
    private DrillMetadata drillId;


    @Column(name = "drill_score")
    private double drillScore;

    @Column(name = "is_pass")
    private boolean isPass;


    @Column(name = "total_correct")
    private int totalCorrect;

    @Column(name = "total_wrong")
    private int totalWrong;

    @Column(name = "crtn_date")
    @CreationTimestamp
    private LocalDateTime crtnDate;

    @OneToMany(mappedBy = "challengeId", cascade = CascadeType.ALL)
    private List<DrillChallengeScores> drillChallengeScoresList;

    @Column(name = "drill_type")
    private String drillType;

    @Column(name = "status")
    private int status;
}
