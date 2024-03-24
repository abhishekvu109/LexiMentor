package com.abhi.leximentor.inventory.entities.inv;


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
@ToString(exclude = {"language", "synonyms", "antonyms", "meanings", "examples", "partsOfSpeeches"})
@Entity
@Table(name = "inv_word_metadata")
public class WordMetadata {

    @Id
    @Column(name = "word_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;


    @Column(name = "uuid")
    private String uuid;

    @Column(name = "ref_id")
    private long refId;

    @Column(name = "word", nullable = false)
    private String word;

    @ManyToOne
    @JoinColumn(name = "languge_id")
    private Language language;

    @Column(name = "pos")
    private String pos;

    @Column(name = "source")
    private String source;

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

    @OneToMany(mappedBy = "wordId", cascade = CascadeType.ALL)
    private List<PartsOfSpeech> partsOfSpeeches;
}
