package com.wiggens.api.visit.dto;

import lombok.Data;

@Data
public class VisitCompleteRequest {
    private String visitNotes;
    private String vitals;
    private String careChanges;
}

