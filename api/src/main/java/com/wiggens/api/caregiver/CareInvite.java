package com.wiggens.api.caregiver;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "care_invites")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareInvite {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String ownerEmail;
    @Column(nullable = false)
    private String inviteeEmail;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @Column(nullable = false, unique = true)
    private String token;
    @Column(nullable = false)
    private Instant createdAt;
    private Instant acceptedAt;
}

