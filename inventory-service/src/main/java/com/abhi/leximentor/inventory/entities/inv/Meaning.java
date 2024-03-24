package com.abhi.leximentor.inventory.entities.inv;

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
@ToString(exclude = {"wordId"})
@Entity
@Table(name = "inv_meaning")
public class Meaning {

    @Id
    @Column(name = "meaning_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "ref_id")
    private long refId;

    @Column(name = "source")
    private String source;

    @ManyToOne
    @JoinColumn(name = "word_id")
    private WordMetadata wordId;

    @Column(name = "definition", length = 5000)
    private String definition;

    @Column(name = "crtn_date")
    @CreationTimestamp
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime crtnDate;

    @Column(name = "last_upd_date")
    @UpdateTimestamp
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime lastUpdDate;

}
