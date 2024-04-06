package com.abhi.synapster.inventory.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.List;

@Data
@ToString
@Builder
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "synapster_inv_subject")
public class Subject {

    @Id
    @Column(name = "subject_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "ref_id", unique = true)
    private long refId;

    @Column(name = "uuid", unique = true)
    private String uuid;

    @CreationTimestamp
    @Column(name = "crtn_date")
    private LocalDateTime crtnDate;

    @UpdateTimestamp
    @Column(name = "last_upd_date")
    private LocalDateTime lastUpdDate;

    @Column(name = "name")
    private String name;

    @Column(name = "status")
    private int status;

    @Column(name = "description")
    private String description;

    @Column(name = "category")
    private String category;

    @OneToMany(mappedBy = "topic",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Topic> topicList;


}
