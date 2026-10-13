package com.abhi.leximentor.leximentor.entities.inv;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Builder
@Data
@ToString(exclude = {"word"})
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "parts_of_speech")
public class PartsOfSpeech {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private long id;

    @Column(name = "`key`", unique = true)
    private String key;

    @ManyToOne
    @JoinColumn(name = "word_id")
    private WordMetadata word;

    @Column(name = "source")
    private String source;

    @Column(name = "pos")
    private String pos;

    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}

