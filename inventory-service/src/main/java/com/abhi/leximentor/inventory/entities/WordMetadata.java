package com.abhi.leximentor.inventory.entities;


import com.abhi.leximentor.inventory.constants.PartsOfSpeech;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
@EqualsAndHashCode
@ToString
@Entity
@Table(name = "word_metadata")
public class WordMetadata {

    @Id
    @Column(name = "word_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;


    @Column(name = "b_key")
    private String key;

    @Column(name = "word", nullable = false)
    private String word;

    @ManyToOne
    @JoinColumn(name = "languge_id")
    private Language language;

    @Column(name = "pos")
    private PartsOfSpeech pos;

    @Column(name = "pronunciation")
    private String pronunciation;

    @Column(name = "category")
    private String category;

    @Column(name = "crtn_date")
    @CreationTimestamp
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime crtnDate;

    @Column(name = "last_upd_date")
    @UpdateTimestamp
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime lastUpdDate;

    @Column(name = "status")
    private int status;

    @OneToMany(mappedBy = "wordId", cascade = CascadeType.ALL)
    private List<Synonym> synonyms;

    @OneToMany(mappedBy = "wordId", cascade = CascadeType.ALL)
    private List<Antonym> antonyms;

    @OneToMany(mappedBy = "wordId", cascade = CascadeType.ALL)
    private List<Meaning> meanings;

    @OneToMany(mappedBy = "wordId", cascade = CascadeType.ALL)
    private List<Example> examples;
}
