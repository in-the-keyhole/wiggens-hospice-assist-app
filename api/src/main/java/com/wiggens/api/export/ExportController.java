package com.wiggens.api.export;

import com.wiggens.api.checklist.CareTaskCompletionRepository;
import com.wiggens.api.contact.ContactRepository;
import com.wiggens.api.medication.MedicationLogRepository;
import com.wiggens.api.medication.MedicationRepository;
import com.wiggens.api.symptom.SymptomRepository;
import com.wiggens.api.visit.VisitRepository;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

@RestController
@RequestMapping("/codex-example/api/v1/exports")
@RequiredArgsConstructor
public class ExportController {
    private final MedicationRepository medicationRepository;
    private final MedicationLogRepository medicationLogRepository;
    private final SymptomRepository symptomRepository;
    private final VisitRepository visitRepository;
    private final CareTaskCompletionRepository completionRepository;
    private final ContactRepository contactRepository;

    @GetMapping(value = "/summary.csv")
    public void summaryCsv(@AuthenticationPrincipal UserDetails user,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
                           @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to,
                           HttpServletResponse res) throws IOException {
        String email = user.getUsername();
        StringBuilder sb = new StringBuilder();
        sb.append("Summary for ").append(email).append(" from ").append(from).append(" to ").append(to).append("\n\n");

        sb.append("[Med Logs]\n");
        sb.append("time,medication,reason,amount,notes\n");
        var meds = medicationRepository.findByPatientProfileUserEmailIgnoreCaseAndActiveTrue(email);
        meds.forEach(m -> medicationLogRepository.findAll().stream()
                .filter(l -> l.getMedication().getId().equals(m.getId()))
                .filter(l -> (l.getAt().isAfter(from) || l.getAt().equals(from)) && l.getAt().isBefore(to))
                .forEach(l -> sb.append(l.getAt()).append(',').append(m.getName()).append(',')
                        .append(escape(l.getReason())).append(',')
                        .append(escape(l.getAmount())).append(',')
                        .append(escape(l.getNotes())).append('\n')));
        sb.append("\n");

        sb.append("[Symptoms]\n");
        sb.append("time,tags,notes,pain\n");
        symptomRepository.findByPatientProfileUserEmailIgnoreCaseOrderByAtDesc(email).stream()
                .filter(e -> (e.getAt().isAfter(from) || e.getAt().equals(from)) && e.getAt().isBefore(to))
                .forEach(e -> sb.append(e.getAt()).append(',')
                        .append(escape(e.getTags())).append(',')
                        .append(escape(e.getNotes())).append(',')
                        .append(e.getPainScore()==null?"":e.getPainScore()).append('\n'));
        sb.append("\n");

        sb.append("[Visits]\n");
        sb.append("time,notes,status\n");
        visitRepository.findByPatientProfileUserEmailIgnoreCaseAndAtBeforeOrderByAtDesc(email, java.time.LocalDateTime.now()).stream()
                .filter(v -> v.getAt().isAfter(from.atZone(java.time.ZoneId.systemDefault()).toLocalDateTime()))
                .forEach(v -> sb.append(v.getAt()).append(',')
                        .append(escape(v.getNotes())).append(',')
                        .append(v.getStatus()).append('\n'));
        sb.append("\n");

        sb.append("[Checklist]\n");
        sb.append("time,task\n");
        completionRepository.findByTaskPatientProfileUserEmailIgnoreCaseAndCompletedAtBetweenOrderByCompletedAtDesc(email, from, to)
                .forEach(c -> sb.append(c.getCompletedAt()).append(',').append(escape(c.getTask().getName())).append('\n'));
        sb.append("\n");

        sb.append("[Contacts]\n");
        sb.append("name,role,phone\n");
        contactRepository.findByPatientProfileUserEmailIgnoreCaseOrderByNameAsc(email)
                .forEach(c -> sb.append(escape(c.getName())).append(',').append(c.getRole()).append(',').append(c.getPhone()).append('\n'));

        byte[] bytes = sb.toString().getBytes(StandardCharsets.UTF_8);
        res.setContentType("text/csv; charset=utf-8");
        res.setHeader("Content-Disposition", "attachment; filename=summary.csv");
        res.getOutputStream().write(bytes);
    }

    private String escape(String s) {
        if (s == null) return "";
        if (s.contains(",") || s.contains("\n") || s.contains("\"")) {
            return '"' + s.replace("\"", "\"\"") + '"';
        }
        return s;
    }
}

