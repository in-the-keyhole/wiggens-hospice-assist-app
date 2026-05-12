package com.wiggens.api.checklist.dto;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class CompletionEntryResponse {
    private Long id;
    private Long taskId;
    private String taskName;
    private Instant completedAt;
}

