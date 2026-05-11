package com.wiggens.api.patient.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDate;

@Data
@Builder
public class PatientProfileResponse {
    private Long id;
    private String fullName;
    private String contactEmail;
    private String contactPhone;
    private LocalDate dateOfBirth;
    private String hospiceOrganization;
    private String primaryPhysician;
    private String allergies;
    private String careDirectives;
    private Instant createdAt;
    private Instant updatedAt;
}

