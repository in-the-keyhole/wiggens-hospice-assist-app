package com.wiggens.api.symptom;

import com.wiggens.api.audit.AuditEntry;
import com.wiggens.api.audit.AuditRepository;
import com.wiggens.api.patient.PatientProfileRepository;
import com.wiggens.api.symptom.dto.SymptomEntryRequest;
import com.wiggens.api.symptom.dto.SymptomEntryResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SymptomService {
    private final SymptomRepository repo;
    private final PatientProfileRepository patientRepo;
    private final AuditRepository auditRepo;

    @Transactional
    public SymptomEntryResponse add(String userEmail, SymptomEntryRequest req) {
        var patient = patientRepo.findByUserEmailIgnoreCase(userEmail).orElseThrow();
        var now = Instant.now();
        var entry = SymptomEntry.builder()
                .patientProfile(patient)
                .at(req.getAt())
                .tags(req.getTags() == null ? null : String.join(",", req.getTags()))
                .notes(req.getNotes())
                .painScore(req.getPainScore())
                .temperatureC(req.getTemperatureC())
                .bloodPressure(req.getBloodPressure())
                .pulse(req.getPulse())
                .respiration(req.getRespiration())
                .photoUrl(req.getPhotoUrl())
                .createdAt(now)
                .updatedAt(now)
                .build();
        var saved = repo.save(entry);
        audit("CREATE", userEmail, saved.getId());
        return toDto(saved);
    }

    public List<SymptomEntryResponse> list(String userEmail, Instant from, Instant to, List<String> tags) {
        List<SymptomEntry> entries;
        if (from != null && to != null) {
            entries = repo.findByPatientProfileUserEmailIgnoreCaseAndAtBetweenOrderByAtDesc(userEmail, from, to);
        } else {
            entries = repo.findByPatientProfileUserEmailIgnoreCaseOrderByAtDesc(userEmail);
        }
        var tagSet = tags == null ? Collections.<String>emptyList() : tags;
        return entries.stream()
                .filter(e -> tagSet.isEmpty() || hasAnyTag(e.getTags(), tagSet))
                .map(this::toDto)
                .toList();
    }

    private boolean hasAnyTag(String csv, List<String> tags) {
        if (csv == null || csv.isBlank()) return false;
        var existing = Arrays.stream(csv.split(",")).map(String::trim).map(String::toLowerCase).toList();
        for (var t : tags) {
            if (existing.contains(t.toLowerCase())) return true;
        }
        return false;
    }

    private void audit(String action, String email, Long id) {
        auditRepo.save(AuditEntry.builder()
                .actorEmail(email)
                .action(action)
                .entity("SymptomEntry")
                .entityId(id)
                .at(Instant.now())
                .build());
    }

    private SymptomEntryResponse toDto(SymptomEntry e) {
        List<String> tags = e.getTags() == null || e.getTags().isBlank()
                ? List.of()
                : Arrays.stream(e.getTags().split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();
        return SymptomEntryResponse.builder()
                .id(e.getId())
                .at(e.getAt())
                .tags(tags)
                .notes(e.getNotes())
                .painScore(e.getPainScore())
                .temperatureC(e.getTemperatureC())
                .bloodPressure(e.getBloodPressure())
                .pulse(e.getPulse())
                .respiration(e.getRespiration())
                .photoUrl(e.getPhotoUrl())
                .createdAt(e.getCreatedAt())
                .updatedAt(e.getUpdatedAt())
                .build();
    }
}

