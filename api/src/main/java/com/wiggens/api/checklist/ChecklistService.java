package com.wiggens.api.checklist;

import com.wiggens.api.audit.AuditEntry;
import com.wiggens.api.audit.AuditRepository;
import com.wiggens.api.checklist.dto.CareTaskRequest;
import com.wiggens.api.checklist.dto.CareTaskResponse;
import com.wiggens.api.checklist.dto.CompleteTaskRequest;
import com.wiggens.api.checklist.dto.CompletionEntryResponse;
import com.wiggens.api.patient.PatientProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChecklistService {
    private final CareTaskRepository taskRepo;
    private final CareTaskCompletionRepository completionRepo;
    private final PatientProfileRepository patientRepo;
    private final AuditRepository auditRepo;

    @Transactional
    public CareTaskResponse addTask(String userEmail, CareTaskRequest req) {
        var patient = patientRepo.findByUserEmailIgnoreCase(userEmail).orElseThrow();
        var now = Instant.now();
        var t = CareTask.builder()
                .patientProfile(patient)
                .name(req.getName())
                .frequencyType(req.getFrequencyType())
                .timesPerDay(req.getTimesPerDay())
                .daysOfWeek(req.getDaysOfWeek())
                .notes(req.getNotes())
                .active(true)
                .createdAt(now)
                .updatedAt(now)
                .build();
        var saved = taskRepo.save(t);
        audit("CREATE_TASK", userEmail, saved.getId());
        return toDto(saved);
    }

    public List<CareTaskResponse> listTasks(String userEmail) {
        return taskRepo.findByPatientProfileUserEmailIgnoreCaseAndActiveTrue(userEmail)
                .stream().map(this::toDto).toList();
    }

    @Transactional
    public void completeTask(String userEmail, Long taskId, CompleteTaskRequest req) {
        var task = taskRepo.findById(taskId).orElseThrow();
        var c = CareTaskCompletion.builder()
                .task(task)
                .completedAt(req.getAt())
                .completedBy(userEmail)
                .build();
        completionRepo.save(c);
        audit("COMPLETE_TASK", userEmail, taskId);
    }

    public List<CompletionEntryResponse> history(String userEmail, Instant from, Instant to) {
        if (from == null) from = Instant.now().minusSeconds(7*24*3600);
        if (to == null) to = Instant.now();
        return completionRepo.findByTaskPatientProfileUserEmailIgnoreCaseAndCompletedAtBetweenOrderByCompletedAtDesc(userEmail, from, to)
                .stream()
                .map(c -> CompletionEntryResponse.builder()
                        .id(c.getId())
                        .taskId(c.getTask().getId())
                        .taskName(c.getTask().getName())
                        .completedAt(c.getCompletedAt())
                        .build())
                .toList();
    }

    private void audit(String action, String email, Long id) {
        auditRepo.save(AuditEntry.builder()
                .actorEmail(email)
                .action(action)
                .entity("CareTask")
                .entityId(id)
                .at(Instant.now())
                .build());
    }

    private CareTaskResponse toDto(CareTask t) {
        return CareTaskResponse.builder()
                .id(t.getId())
                .name(t.getName())
                .frequencyType(t.getFrequencyType())
                .timesPerDay(t.getTimesPerDay())
                .daysOfWeek(t.getDaysOfWeek())
                .notes(t.getNotes())
                .active(t.isActive())
                .createdAt(t.getCreatedAt())
                .updatedAt(t.getUpdatedAt())
                .build();
    }
}

