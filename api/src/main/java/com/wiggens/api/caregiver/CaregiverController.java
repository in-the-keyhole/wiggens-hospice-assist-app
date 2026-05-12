package com.wiggens.api.caregiver;

import com.wiggens.api.auth.dto.AuthResponse;
import com.wiggens.api.user.User;
import com.wiggens.api.user.UserRepository;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/codex-example/api/v1/caregivers")
@RequiredArgsConstructor
public class CaregiverController {
    private final CareInviteRepository invites;
    private final UserRepository users;
    private final PasswordEncoder encoder;
    private final com.wiggens.api.security.JwtService jwtService;
    private final com.wiggens.api.audit.AuditRepository auditRepo;

    @PostMapping("/invite")
    public ResponseEntity<Map<String, String>> invite(@AuthenticationPrincipal UserDetails owner,
                                                      @RequestBody InviteRequest req) {
        String token = UUID.randomUUID().toString();
        CareInvite inv = CareInvite.builder()
                .ownerEmail(owner.getUsername())
                .inviteeEmail(req.getEmail().toLowerCase())
                .role(req.getRole())
                .token(token)
                .createdAt(Instant.now())
                .build();
        invites.save(inv);
        audit("INVITE_CREATE", owner.getUsername(), inv.getId());
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/accept")
    public ResponseEntity<AuthResponse> accept(@RequestBody AcceptRequest req) {
        CareInvite inv = invites.findByToken(req.getToken()).orElseThrow();
        if (inv.getAcceptedAt() != null) throw new IllegalStateException("Invite already accepted");
        if (!users.existsByEmailIgnoreCase(inv.getInviteeEmail())) {
            users.save(User.builder()
                    .email(inv.getInviteeEmail())
                    .passwordHash(encoder.encode(req.getPassword()))
                    .createdAt(Instant.now())
                    .build());
        }
        inv.setAcceptedAt(Instant.now());
        invites.save(inv);
        audit("INVITE_ACCEPT", inv.getInviteeEmail(), inv.getId());
        String jwt = jwtService.generateToken(inv.getInviteeEmail());
        return ResponseEntity.ok(new AuthResponse(jwt));
    }

    private void audit(String action, String email, Long id) {
        auditRepo.save(com.wiggens.api.audit.AuditEntry.builder()
                .actorEmail(email)
                .action(action)
                .entity("CareInvite")
                .entityId(id)
                .at(Instant.now())
                .build());
    }

    @Data
    public static class InviteRequest {
        @Email @NotBlank
        private String email;
        private Role role;
    }

    @Data
    public static class AcceptRequest {
        @NotBlank
        private String token;
        @NotBlank
        private String password;
    }
}

