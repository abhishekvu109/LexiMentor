package com.abhi.synapster.inventory.entities;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.print.Doc;
import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "synapster_inv_content")
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private long id;

    @Column(name = "ref_id", unique = true)
    private long refId;

    @Column(name = "uuid", unique = true)
    private String uuid;

    @Column(name = "main_heading")
    private String mainHeading;

    @CreationTimestamp
    @Column(name = "crtn_date")
    private LocalDateTime crtnDate;

    @UpdateTimestamp
    @Column(name = "last_upd_date")
    private LocalDateTime lastUpdDate;

    @Column(name = "status")
    private int status;

    @ManyToOne
    @JoinColumn(name = "topic_id")
    private Topic topic;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "content", fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    private List<Document> documentList;
}
