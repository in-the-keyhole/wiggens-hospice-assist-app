package com.wiggens.api.visit;

import com.wiggens.api.audit.AuditEntry;
import com.wiggens.api.audit.AuditRepository;
import com.wiggens.api.patient.PatientProfileRepository;
import com.wiggens.api.visit.dto.VisitCompleteRequest;
import com.wiggens.api.visit.dto.VisitRequest;
import com.wiggens.api.visit.dto.VisitResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VisitService {
    private final VisitRepository repo;
    private final PatientProfileRepository patientRepo;
    private final AuditRepository auditRepo;

    @Transactional
    public VisitResponse add(String userEmail, VisitRequest req) {
        var patient = patientRepo.findByUserEmailIgnoreCase(userEmail).orElseThrow();
        var now = Instant.now();
        var v = Visit.builder()
                .patientProfile(patient)
                .at(req.getAt())
                .providerRole(req.getProviderRole())
                .notes(req.getNotes())
                .status(VisitStatus.UPCOMING)
                .createdAt(now)
                .updatedAt(now)
                .build();
        var saved = repo.save(v);
        audit("CREATE", userEmail, saved.getId());
        return toDto(saved);
    }

    public List<VisitResponse> upcoming(String userEmail) {
        return repo.findByPatientProfileUserEmailIgnoreCaseAndStatusOrderByAtAsc(userEmail, VisitStatus.UPCOMING)
                .stream().map(this::toDto).toList();
    }

    public List<VisitResponse> past(String userEmail) {
        return repo.findByPatientProfileUserEmailIgnoreCaseAndAtBeforeOrderByAtDesc(userEmail, LocalDateTime.now())
                .stream().filter(v -> v.getStatus() == VisitStatus.COMPLETED).map(this::toDto).toList();
    }

    @Transactional
    public VisitResponse complete(String userEmail, Long id, VisitCompleteRequest req) {
        var v = repo.findById(id).orElseThrow();
        v.setVisitNotes(req.getVisitNotes());
        v.setVitals(req.getVitals());
        v.setCareChanges(req.getCareChanges());
        v.setStatus(VisitStatus.COMPLETED);
        v.setUpdatedAt(Instant.now());
        var saved = repo.save(v);
        audit("COMPLETE", userEmail, id);
        return toDto(saved);
    }

    private void audit(String action, String email, Long id) {
        auditRepo.save(AuditEntry.builder()
                .actorEmail(email)
                .action(action)
                .entity("Visit")
                .entityId(id)
                .at(Instant.now())
                .build());
    }

    private VisitResponse toDto(Visit v) {
        return VisitResponse.builder()
                .id(v.getId())
                .at(v.getAt())
                .providerRole(v.getProviderRole())
                .notes(v.getNotes())
                .status(v.getStatus())
                .visitNotes(v.getVisitNotes())
                .vitals(v.getVitals())
                .careChanges(v.getCareChanges())
                .createdAt(v.getCreatedAt())
                .updatedAt(v.getUpdatedAt())
                .build();
    }
}

