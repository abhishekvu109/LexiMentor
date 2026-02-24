package com.abhi.leximentor.leximentor.entities.inv;


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
    @Column(name = "id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private long id;

    @Column(name = "key")
    private String key;

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

    @Column(name = "created_at")
    @CreationTimestamp
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @UpdateTimestamp
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime updatedAt;

    @Column(name = "status")
    private int status;

    @Column(name = "mnemonic")
    private String mnemonic;

    @Column(name = "local_meaning")
    private String localMeaning;

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL)
    private List<Synonym> synonyms;

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL)
    private List<Antonym> antonyms;

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL)
    private List<Meaning> meanings;

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL)
    private List<Example> examples;

    @OneToMany(mappedBy = "word", cascade = CascadeType.ALL)
    private List<PartsOfSpeech> partsOfSpeeches;
}
