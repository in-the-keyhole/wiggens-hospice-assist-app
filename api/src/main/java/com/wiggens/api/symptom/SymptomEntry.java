package com.wiggens.api.symptom;

import com.wiggens.api.patient.PatientProfile;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "symptom_entries")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SymptomEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PatientProfile patientProfile;

    @Column(nullable = false)
    private Instant at; // UTC timestamp of entry

    private String tags; // comma-separated symptom tags

    private String notes;

    private Integer painScore; // 0-10

    private Double temperatureC; // optional
    private String bloodPressure; // e.g., 120/80
    private Integer pulse; // bpm
    private Integer respiration; // breaths per minute

    private String photoUrl; // optional photo reference

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;
}

