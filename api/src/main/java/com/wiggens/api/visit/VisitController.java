package com.wiggens.api.visit;

import com.wiggens.api.visit.dto.VisitCompleteRequest;
import com.wiggens.api.visit.dto.VisitRequest;
import com.wiggens.api.visit.dto.VisitResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/codex-example/api/v1/visits")
@RequiredArgsConstructor
public class VisitController {
    private final VisitService service;

    @PostMapping("/me")
    public ResponseEntity<VisitResponse> add(@AuthenticationPrincipal UserDetails user, @Valid @RequestBody VisitRequest req) {
        return ResponseEntity.ok(service.add(user.getUsername(), req));
    }

    @GetMapping("/me")
    public ResponseEntity<List<VisitResponse>> upcoming(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(service.upcoming(user.getUsername()));
    }

    @GetMapping("/me/past")
    public ResponseEntity<List<VisitResponse>> past(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(service.past(user.getUsername()));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<VisitResponse> complete(@AuthenticationPrincipal UserDetails user, @PathVariable Long id, @RequestBody VisitCompleteRequest req) {
        return ResponseEntity.ok(service.complete(user.getUsername(), id, req));
    }
}

