package com.wiggens.api.medication;

import com.wiggens.api.audit.AuditEntry;
import com.wiggens.api.audit.AuditRepository;
import com.wiggens.api.medication.dto.MedicationLogRequest;
import com.wiggens.api.medication.dto.MedicationRequest;
import com.wiggens.api.medication.dto.MedicationResponse;
import com.wiggens.api.patient.PatientProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MedicationService {
    private final MedicationRepository medRepo;
    private final MedicationLogRepository logRepo;
    private final PatientProfileRepository patientRepo;
    private final AuditRepository auditRepo;

    @Transactional
    public MedicationResponse add(String userEmail, MedicationRequest req) {
        var patient = patientRepo.findByUserEmailIgnoreCase(userEmail).orElseThrow();
        var now = Instant.now();
        var m = Medication.builder()
                .patientProfile(patient)
                .name(req.getName())
                .strength(req.getStrength())
                .route(req.getRoute())
                .dosageInstructions(req.getDosageInstructions())
                .scheduleType(req.getScheduleType())
                .scheduleTimes(req.getScheduleType() == MedicationScheduleType.SCHEDULED ? req.getScheduleTimes() : null)
                .prescribingInfo(req.getPrescribingInfo())
                .specialInstructions(req.getSpecialInstructions())
                .inventoryCount(req.getInventoryCount())
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
        var saved = medRepo.save(m);
        audit("CREATE", userEmail, saved.getId());
        return toDto(saved);
    }

    public List<MedicationResponse> listActive(String userEmail) {
        return medRepo.findByPatientProfileUserEmailIgnoreCaseAndActiveTrue(userEmail)
                .stream()
                .sorted(Comparator.comparing(this::sortKey))
                .map(this::toDto)
                .toList();
    }

    @Transactional
    public void archive(String userEmail, Long id) {
        var m = medRepo.findById(id).orElseThrow();
        m.setActive(false);
        m.setUpdatedAt(Instant.now());
        medRepo.save(m);
        audit("ARCHIVE", userEmail, id);
    }

    @Transactional
    public void logDose(String userEmail, Long id, MedicationLogRequest req) {
        var m = medRepo.findById(id).orElseThrow();
        if (m.getScheduleType() == MedicationScheduleType.PRN && (req.getReason() == null || req.getReason().isBlank())) {
            throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST,
                    "Reason required for PRN medication");
        }
        var log = MedicationLog.builder()
                .medication(m)
                .at(req.getAt())
                .reason(req.getReason())
                .build();
        logRepo.save(log);
        audit("LOG", userEmail, id);
    }

    private String sortKey(Medication m) {
        if (m.getScheduleType() == MedicationScheduleType.PRN) return "ZZZ"; // list PRN last
        var times = m.getScheduleTimes();
        if (times == null || times.isBlank()) return "ZZZ";
        try {
            var first = times.split(",")[0].trim();
            var t = LocalTime.parse(first);
            return t.toString();
        } catch (Exception e) {
            return times;
        }
    }

    private void audit(String action, String email, Long id) {
        auditRepo.save(AuditEntry.builder()
                .actorEmail(email)
                .action(action)
                .entity("Medication")
                .entityId(id)
                .at(Instant.now())
                .build());
    }

    private MedicationResponse toDto(Medication m) {
        return MedicationResponse.builder()
                .id(m.getId())
                .name(m.getName())
                .strength(m.getStrength())
                .route(m.getRoute())
                .dosageInstructions(m.getDosageInstructions())
                .scheduleType(m.getScheduleType())
                .scheduleTimes(m.getScheduleTimes())
                .prescribingInfo(m.getPrescribingInfo())
                .specialInstructions(m.getSpecialInstructions())
                .inventoryCount(m.getInventoryCount())
                .active(m.isActive())
                .createdAt(m.getCreatedAt())
                .updatedAt(m.getUpdatedAt())
                .build();
    }
}
