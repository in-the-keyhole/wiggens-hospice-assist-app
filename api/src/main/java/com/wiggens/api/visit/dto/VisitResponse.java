package com.wiggens.api.visit.dto;

import com.wiggens.api.visit.VisitStatus;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.time.LocalDateTime;

@Data
@Builder
public class VisitResponse {
    private Long id;
    private LocalDateTime at;
    private String providerRole;
    private String notes;
    private VisitStatus status;
    private String visitNotes;
    private String vitals;
    private String careChanges;
    private Instant createdAt;
    private Instant updatedAt;
}

