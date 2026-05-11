package com.wiggens.api.visit;

import com.wiggens.api.patient.PatientProfile;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Table(name = "visits")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Visit {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PatientProfile patientProfile;

    @Column(nullable = false)
    private LocalDateTime at;

    @Column(nullable = false)
    private String providerRole; // Nurse, Social Worker, etc.

    private String notes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VisitStatus status;

    private String visitNotes;
    private String vitals;
    private String careChanges;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;
}

