package com.abhi.leximentor.inventory.entities.inv;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@Entity
@Table(name = "inv_languages")
public class Language {

    @Id
    @Column(name = "language_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "b_key")
    private String key;

    @Column(name = "source")
    private String source;

    @Column(name = "langauge")
    private String language;

    @Column(name = "status")
    private int status;

    @Column(name = "crtn_date")
    @CreationTimestamp
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime crtnDate;

    @Column(name = "last_upd_date")
    @UpdateTimestamp
    @Setter(AccessLevel.PRIVATE)
    private LocalDateTime lastUpdDate;
}