package com.abhi.leximentor.inventory.entities.drill;

import com.abhi.leximentor.inventory.entities.inv.WordMetadata;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "drill_set")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class DrillSet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "drill_set_id")
    private long id;

    @Column(name = "ref_id", nullable = false, unique = true)
    private String refId;

    @ManyToOne
    @JoinColumn(name = "drill_metadata_id")
    private DrillMetadata drillId;

    @CreationTimestamp
    @Column(name = "crtn_date")
    private LocalDateTime crtndate;

    @ManyToOne
    @JoinColumn(name = "word_id")
    private WordMetadata wordId;
}