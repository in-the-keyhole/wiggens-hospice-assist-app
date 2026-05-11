package com.wiggens.api.symptom;

import com.wiggens.api.symptom.dto.SymptomEntryRequest;
import com.wiggens.api.symptom.dto.SymptomEntryResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/codex-example/api/v1/symptoms")
@RequiredArgsConstructor
public class SymptomController {
    private final SymptomService service;

    @PostMapping("/me")
    public ResponseEntity<SymptomEntryResponse> add(@AuthenticationPrincipal UserDetails user,
                                                    @Valid @RequestBody SymptomEntryRequest req) {
        return ResponseEntity.ok(service.add(user.getUsername(), req));
    }

    @GetMapping("/me")
    public ResponseEntity<List<SymptomEntryResponse>> list(
            @AuthenticationPrincipal UserDetails user,
            @RequestParam(value = "from", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam(value = "to", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
            @RequestParam(value = "tag", required = false) List<String> tags
    ) {
        return ResponseEntity.ok(service.list(user.getUsername(), from, to, tags));
    }
}

