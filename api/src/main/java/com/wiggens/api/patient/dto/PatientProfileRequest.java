package com.wiggens.api.patient.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientProfileRequest {
    @NotBlank
    private String fullName;

    @Email
    private String contactEmail;

    private String contactPhone;

    private LocalDate dateOfBirth;
    private String hospiceOrganization;
    private String primaryPhysician;
    private String allergies;
    private String careDirectives;
}

