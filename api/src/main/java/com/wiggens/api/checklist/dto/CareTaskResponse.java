package com.wiggens.api.checklist.dto;

import com.wiggens.api.checklist.FrequencyType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CareTaskResponse {
    private Long id;
    private String name;
    private FrequencyType frequencyType;
    private Integer timesPerDay;
    private String daysOfWeek;
    private String notes;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}

