package com.wiggens.api.checklist.dto;

import com.wiggens.api.checklist.FrequencyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CareTaskRequest {
    @NotBlank
    private String name;
    @NotNull
    private FrequencyType frequencyType;
    private Integer timesPerDay;
    private String daysOfWeek; // CSV of MON..SUN
    private String notes;
}

