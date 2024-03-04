package com.abhi.leximentor.inventory.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Builder
@Data
@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "parts_of_speech")
public class PartsOfSpeech {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "b_key", unique = true)
    private String key;

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
