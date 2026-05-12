package com.wiggens.api.checklist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface CareTaskCompletionRepository extends JpaRepository<CareTaskCompletion, Long> {
    List<CareTaskCompletion> findByTaskPatientProfileUserEmailIgnoreCaseAndCompletedAtBetweenOrderByCompletedAtDesc(String email, Instant from, Instant to);
}

