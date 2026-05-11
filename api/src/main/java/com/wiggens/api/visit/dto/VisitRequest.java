package com.wiggens.api.visit.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VisitRequest {
    @NotNull
    private LocalDateTime at;
    @NotBlank
    private String providerRole;
    private String notes;
}

