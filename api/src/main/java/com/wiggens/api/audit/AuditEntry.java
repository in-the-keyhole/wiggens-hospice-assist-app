package com.wiggens.api.audit;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "audit_entries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String actorEmail;

    @Column(nullable = false)
    private String action;

    @Column(nullable = false)
    private String entity;

    @Column(nullable = false)
    private Long entityId;

    @Column(nullable = false)
    private Instant at;
}

