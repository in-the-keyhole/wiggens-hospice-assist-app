package com.wiggens.api.medication;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "medication_logs")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MedicationLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private Medication medication;

    @Column(nullable = false)
    private Instant at; // administration time

    private String reason; // required for PRN

    private String amount; // e.g., 5mg, 2 tabs
    private String notes;
    private Integer painBefore;
    private Integer painAfter;
    private String symptoms;
    private String administeredBy;
    private String photoUrl; // optional reference to uploaded photo
}
