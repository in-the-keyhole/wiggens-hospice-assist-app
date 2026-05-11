package com.wiggens.api.medication.dto;

import com.wiggens.api.medication.MedicationScheduleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MedicationRequest {
    @NotBlank
    private String name;
    private String strength;
    private String route;
    private String dosageInstructions;
    @NotNull
    private MedicationScheduleType scheduleType;
    private String scheduleTimes; // CSV for SCHEDULED
    private String prescribingInfo;
    private String specialInstructions;
    private Integer inventoryCount;
}

