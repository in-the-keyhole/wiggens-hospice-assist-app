package com.wiggens.api.contact.dto;

import com.wiggens.api.contact.ContactRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ContactRequest {
    @NotBlank
    private String name;

    @NotNull
    private ContactRole role;

    @NotBlank
    private String phone;
}

