package com.wiggens.api.medication.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class MedicationLogRequest {
    @NotNull
    private Instant at;
    private String reason; // required for PRN
}

