package com.wiggens.api.medication.dto;

import com.wiggens.api.medication.MedicationScheduleType;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class MedicationResponse {
    private Long id;
    private String name;
    private String strength;
    private String route;
    private String dosageInstructions;
    private MedicationScheduleType scheduleType;
    private String scheduleTimes;
    private String prescribingInfo;
    private String specialInstructions;
    private Integer inventoryCount;
    private boolean active;
    private Instant createdAt;
    private Instant updatedAt;
}

