package com.abhi.saarthi.cashflow.entities;

import com.abhi.saarthi.cashflow.constants.MemberRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "household_member")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HouseholdMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ref_id")
    private long refId;

    @Column(name = "uuid")
    private String uuid;

    @Column(name = "user_id")
    private String user;

    @ManyToOne
    @JoinColumn(name = "household_id")
    private Household household;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private MemberRole role;        // ADMIN, MEMBER, VIEWER

    @Column(name = "status")
    private int status;

    @Column(name = "joining_date")
    @CreationTimestamp
    private LocalDateTime joiningDate;
}