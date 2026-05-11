package com.wiggens.api.patient;

import com.wiggens.api.patient.dto.PatientProfileRequest;
import com.wiggens.api.patient.dto.PatientProfileResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/codex-example/api/v1/patients")
@RequiredArgsConstructor
public class PatientController {
    private final PatientService service;

    @PostMapping
    public ResponseEntity<PatientProfileResponse> create(@AuthenticationPrincipal UserDetails user,
                                                         @Valid @RequestBody PatientProfileRequest req) {
        var res = service.createOrUpdateForUser(user.getUsername(), req, true);
        return ResponseEntity.ok(res);
    }

    @GetMapping("/me")
    public ResponseEntity<PatientProfileResponse> me(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(service.getForUser(user.getUsername()));
    }

    @PutMapping("/me")
    public ResponseEntity<PatientProfileResponse> update(@AuthenticationPrincipal UserDetails user,
                                                         @Valid @RequestBody PatientProfileRequest req) {
        var res = service.createOrUpdateForUser(user.getUsername(), req, false);
        return ResponseEntity.ok(res);
    }
}

