package com.wiggens.api.patient;

import com.wiggens.api.audit.AuditEntry;
import com.wiggens.api.audit.AuditRepository;
import com.wiggens.api.patient.dto.PatientProfileRequest;
import com.wiggens.api.patient.dto.PatientProfileResponse;
import com.wiggens.api.user.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class PatientService {
    private final PatientProfileRepository patientRepo;
    private final UserRepository userRepo;
    private final AuditRepository auditRepo;

    @Transactional
    public PatientProfileResponse createOrUpdateForUser(String userEmail, PatientProfileRequest req, boolean createOnly) {
        var user = userRepo.findByEmailIgnoreCase(userEmail).orElseThrow();
        var existing = patientRepo.findByUserEmailIgnoreCase(userEmail).orElse(null);
        if (existing == null) {
            var now = Instant.now();
            var p = PatientProfile.builder()
                    .user(user)
                    .fullName(req.getFullName())
                    .contactEmail(req.getContactEmail())
                    .contactPhone(req.getContactPhone())
                    .dateOfBirth(req.getDateOfBirth())
                    .hospiceOrganization(req.getHospiceOrganization())
                    .primaryPhysician(req.getPrimaryPhysician())
                    .allergies(req.getAllergies())
                    .careDirectives(req.getCareDirectives())
                    .createdAt(now)
                    .updatedAt(now)
                    .build();
            var saved = patientRepo.save(p);
            audit("CREATE", userEmail, saved.getId());
            return toDto(saved);
        } else {
            if (createOnly) throw new IllegalStateException("Profile already exists");
            existing.setFullName(req.getFullName());
            existing.setContactEmail(req.getContactEmail());
            existing.setContactPhone(req.getContactPhone());
            existing.setDateOfBirth(req.getDateOfBirth());
            existing.setHospiceOrganization(req.getHospiceOrganization());
            existing.setPrimaryPhysician(req.getPrimaryPhysician());
            existing.setAllergies(req.getAllergies());
            existing.setCareDirectives(req.getCareDirectives());
            existing.setUpdatedAt(Instant.now());
            var saved = patientRepo.save(existing);
            audit("UPDATE", userEmail, saved.getId());
            return toDto(saved);
        }
    }

    public PatientProfileResponse getForUser(String userEmail) {
        var p = patientRepo.findByUserEmailIgnoreCase(userEmail).orElseThrow();
        return toDto(p);
    }

    private void audit(String action, String email, Long id) {
        auditRepo.save(AuditEntry.builder()
                .actorEmail(email)
                .action(action)
                .entity("PatientProfile")
                .entityId(id)
                .at(Instant.now())
                .build());
    }

    private PatientProfileResponse toDto(PatientProfile p) {
        return PatientProfileResponse.builder()
                .id(p.getId())
                .fullName(p.getFullName())
                .contactEmail(p.getContactEmail())
                .contactPhone(p.getContactPhone())
                .dateOfBirth(p.getDateOfBirth())
                .hospiceOrganization(p.getHospiceOrganization())
                .primaryPhysician(p.getPrimaryPhysician())
                .allergies(p.getAllergies())
                .careDirectives(p.getCareDirectives())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}

