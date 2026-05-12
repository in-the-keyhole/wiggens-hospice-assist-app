package com.wiggens.api.checklist;

import com.wiggens.api.checklist.dto.CareTaskRequest;
import com.wiggens.api.checklist.dto.CareTaskResponse;
import com.wiggens.api.checklist.dto.CompleteTaskRequest;
import com.wiggens.api.checklist.dto.CompletionEntryResponse;
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
@RequestMapping("/codex-example/api/v1/checklist")
@RequiredArgsConstructor
public class ChecklistController {
    private final ChecklistService service;

    @PostMapping("/me")
    public ResponseEntity<CareTaskResponse> add(@AuthenticationPrincipal UserDetails user,
                                                @Valid @RequestBody CareTaskRequest req) {
        return ResponseEntity.ok(service.addTask(user.getUsername(), req));
    }

    @GetMapping("/me")
    public ResponseEntity<List<CareTaskResponse>> list(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(service.listTasks(user.getUsername()));
    }

    @PostMapping("/{id}/complete")
    public ResponseEntity<Void> complete(@AuthenticationPrincipal UserDetails user,
                                         @PathVariable Long id,
                                         @Valid @RequestBody CompleteTaskRequest req) {
        service.completeTask(user.getUsername(), id, req);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/history")
    public ResponseEntity<List<CompletionEntryResponse>> history(@AuthenticationPrincipal UserDetails user,
                                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
                                                                 @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return ResponseEntity.ok(service.history(user.getUsername(), from, to));
    }
}

