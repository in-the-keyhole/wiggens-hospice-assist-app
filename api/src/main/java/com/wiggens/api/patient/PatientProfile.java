package com.wiggens.api.patient;

import com.wiggens.api.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "patient_profiles", uniqueConstraints = @UniqueConstraint(name = "uniq_patient_user", columnNames = {"user_id"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PatientProfile {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private User user;

    @Column(nullable = false)
    private String fullName;

    private String contactEmail;
    private String contactPhone;

    private LocalDate dateOfBirth;
    private String hospiceOrganization;
    private String primaryPhysician;
    private String allergies;
    private String careDirectives;

    @Column(nullable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;
}

