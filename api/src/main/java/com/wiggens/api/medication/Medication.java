package com.wiggens.api.medication;

import com.wiggens.api.patient.PatientProfile;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "medications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Medication {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    private PatientProfile patientProfile;

    @Column(nullable = false)
    private String name;

    private String strength; // e.g., 5mg
    private String route; // e.g., oral, IV
    private String dosageInstructions; // free text

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MedicationScheduleType scheduleType; // SCHEDULED or PRN

    private String scheduleTimes; // CSV HH:mm times for SCHEDULED
    private String prescribingInfo;
    private String specialInstructions;
    private Integer inventoryCount;

    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;
}

