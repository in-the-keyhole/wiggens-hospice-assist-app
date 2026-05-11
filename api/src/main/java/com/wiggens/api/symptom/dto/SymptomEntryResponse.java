package com.wiggens.api.symptom.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class SymptomEntryResponse {
    private Long id;
    private Instant at;
    private List<String> tags;
    private String notes;
    private Integer painScore;
    private Double temperatureC;
    private String bloodPressure;
    private Integer pulse;
    private Integer respiration;
    private String photoUrl;
    private Instant createdAt;
    private Instant updatedAt;
}

