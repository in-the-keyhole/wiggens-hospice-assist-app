package com.wiggens.api.medication.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class MedicationLogResponse {
    private Long id;
    private Instant at;
    private String reason;
    private String amount;
    private String notes;
    private Integer painBefore;
    private Integer painAfter;
    private String symptoms;
    private String administeredBy;
    private String photoUrl;
}

