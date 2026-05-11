package com.wiggens.api.symptom;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface SymptomRepository extends JpaRepository<SymptomEntry, Long> {
    List<SymptomEntry> findByPatientProfileUserEmailIgnoreCaseOrderByAtDesc(String email);
    List<SymptomEntry> findByPatientProfileUserEmailIgnoreCaseAndAtBetweenOrderByAtDesc(String email, Instant from, Instant to);
}

