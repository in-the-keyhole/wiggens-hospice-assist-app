package com.wiggens.api.medication;

import com.wiggens.api.medication.dto.MedicationLogRequest;
import com.wiggens.api.medication.dto.MedicationRequest;
import com.wiggens.api.medication.dto.MedicationResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/codex-example/api/v1/medications")
@RequiredArgsConstructor
public class MedicationController {
    private final MedicationService service;

    @GetMapping("/me")
    public ResponseEntity<List<MedicationResponse>> list(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(service.listActive(user.getUsername()));
    }

    @PostMapping("/me")
    public ResponseEntity<MedicationResponse> add(@AuthenticationPrincipal UserDetails user, @Valid @RequestBody MedicationRequest req) {
        return ResponseEntity.ok(service.add(user.getUsername(), req));
    }

    @PutMapping("/{id}/archive")
    public ResponseEntity<Void> archive(@AuthenticationPrincipal UserDetails user, @PathVariable Long id) {
        service.archive(user.getUsername(), id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/logs")
    public ResponseEntity<Void> log(@AuthenticationPrincipal UserDetails user, @PathVariable Long id,
                                    @Valid @RequestBody MedicationLogRequest req) {
        service.logDose(user.getUsername(), id, req);
        return ResponseEntity.noContent().build();
    }
}

