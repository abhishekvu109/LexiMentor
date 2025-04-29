package com.abhi.leximentor.inventory.entities.drill;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"challengeId","drillSetId"})
@Entity
@Table(name = "drill_challenge_score")
public class DrillChallengeScores {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "drill_challenge_score_id")
    private long id;

    @Column(name = "ref_id")
    private long refId;

    @Column(name = "uuid")
    private String uuid;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "drill_challenge_id")
    private DrillChallenge challengeId;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "drill_set_id")
    private DrillSet drillSetId;

    @Column(name = "is_correct")
    private boolean isCorrect;

    @Column(name = "question")
    private String question;

    @Column(name = "response")
    private String response;

    @CreationTimestamp
    @Column(name = "crtn_date")
    private LocalDateTime crtnDate;


    @Column(name = "description", length = 5000)
    private String Description;
}
