package com.wiggens.api.contact;

import com.wiggens.api.audit.AuditEntry;
import com.wiggens.api.audit.AuditRepository;
import com.wiggens.api.contact.dto.ContactRequest;
import com.wiggens.api.contact.dto.ContactResponse;
import com.wiggens.api.patient.PatientProfileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ContactService {
    private final ContactRepository contactRepository;
    private final PatientProfileRepository patientRepository;
    private final AuditRepository auditRepository;

    @Transactional
    public ContactResponse addForCurrentUser(String userEmail, ContactRequest req) {
        var patient = patientRepository.findByUserEmailIgnoreCase(userEmail).orElseThrow();
        var now = Instant.now();
        var contact = Contact.builder()
                .patientProfile(patient)
                .name(req.getName())
                .role(req.getRole())
                .phone(req.getPhone())
                .createdAt(now)
                .updatedAt(now)
                .build();
        var saved = contactRepository.save(contact);
        audit("CREATE", userEmail, saved.getId());
        return toDto(saved);
    }

    public List<ContactResponse> listForCurrentUser(String userEmail) {
        return contactRepository.findByPatientProfileUserEmailIgnoreCaseOrderByNameAsc(userEmail)
                .stream().map(this::toDto).toList();
    }

    private void audit(String action, String email, Long id) {
        auditRepository.save(AuditEntry.builder()
                .actorEmail(email)
                .action(action)
                .entity("Contact")
                .entityId(id)
                .at(Instant.now())
                .build());
    }

    private ContactResponse toDto(Contact c) {
        return ContactResponse.builder()
                .id(c.getId())
                .name(c.getName())
                .role(c.getRole())
                .phone(c.getPhone())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}

