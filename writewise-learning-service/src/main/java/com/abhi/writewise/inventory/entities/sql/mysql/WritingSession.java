package com.abhi.writewise.inventory.entities.sql.mysql;

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
@Table(name = "writewise_llm_topic_master")
public class WritingSession {

    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.PRIVATE)
    private long id;
    @Column(name = "ref_id", nullable = false, unique = true)
    private long refId;
    @Column(name = "uuid", nullable = false, unique = true)
    private String uuid;
    @Column(name = "mongo_topic_id")
    private String mongoTopicId;
    @Column(name = "mongo_topic_response_id")
    private String mongoTopicResponseId;
    @Column(name = "mongo_evaluation_id")
    private String mongoEvaluationId;
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
    @Column(name = "delete_ind")
    private int deleteInd;
}
