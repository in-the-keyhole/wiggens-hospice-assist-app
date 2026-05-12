package com.wiggens.api.checklist.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.Instant;

@Data
public class CompleteTaskRequest {
    @NotNull
    private Instant at;
}

