package com.abhi.leximentor.inventory.entities.inv;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Builder
@Data
@ToString(exclude = {"wordId"})
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "inv_parts_of_speech")
public class PartsOfSpeech {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "uuid", unique = true)
    private String uuid;

    @Column(name = "ref_id")
    private long refId;

    @ManyToOne
    @JoinColumn(name = "word_id")
    private WordMetadata wordId;

    @Column(name = "source")
    private String source;

    @Column(name = "pos")
    private String pos;

    @CreationTimestamp
    @Column(name = "crtn_date")
    private LocalDateTime crtnDate;

    @UpdateTimestamp
    @Column(name = "last_upd_date")
    private LocalDateTime lastUpdDate;

}
