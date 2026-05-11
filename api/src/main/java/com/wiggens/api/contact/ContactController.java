package com.wiggens.api.contact;

import com.wiggens.api.contact.dto.ContactRequest;
import com.wiggens.api.contact.dto.ContactResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/codex-example/api/v1/contacts")
@RequiredArgsConstructor
public class ContactController {
    private final ContactService service;

    @GetMapping("/me")
    public ResponseEntity<List<ContactResponse>> myContacts(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(service.listForCurrentUser(user.getUsername()));
    }

    @PostMapping("/me")
    public ResponseEntity<ContactResponse> add(@AuthenticationPrincipal UserDetails user,
                                               @Valid @RequestBody ContactRequest req) {
        return ResponseEntity.ok(service.addForCurrentUser(user.getUsername(), req));
    }
}

