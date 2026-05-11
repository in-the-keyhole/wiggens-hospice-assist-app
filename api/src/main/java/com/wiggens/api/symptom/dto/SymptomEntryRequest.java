package com.wiggens.api.symptom.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
public class SymptomEntryRequest {
    @NotNull
    private Instant at; // UTC
    private List<String> tags; // optional
    private String notes;
    @Min(0) @Max(10)
    private Integer painScore; // optional but constrained

    private Double temperatureC;
    private String bloodPressure;
    private Integer pulse;
    private Integer respiration;

    private String photoUrl;
}

