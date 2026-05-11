package com.wiggens.api.contact.dto;

import com.wiggens.api.contact.ContactRole;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class ContactResponse {
    private Long id;
    private String name;
    private ContactRole role;
    private String phone;
    private Instant createdAt;
    private Instant updatedAt;
}

