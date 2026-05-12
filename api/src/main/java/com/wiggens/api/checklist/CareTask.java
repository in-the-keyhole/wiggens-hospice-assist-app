package com.wiggens.api.checklist;

import com.wiggens.api.patient.PatientProfile;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "care_tasks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareTask {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private PatientProfile patientProfile;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    private FrequencyType frequencyType;

    private Integer timesPerDay; // for TIMES_PER_DAY
    private String daysOfWeek; // CSV of MON..SUN for DAYS_OF_WEEK
    private String notes;
    private boolean active;

    private Instant createdAt;
    private Instant updatedAt;
}

