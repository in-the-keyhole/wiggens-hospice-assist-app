package com.wiggens.api.visit;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VisitRepository extends JpaRepository<Visit, Long> {
    List<Visit> findByPatientProfileUserEmailIgnoreCaseAndStatusOrderByAtAsc(String email, VisitStatus status);
    List<Visit> findByPatientProfileUserEmailIgnoreCaseAndAtBeforeOrderByAtDesc(String email, LocalDateTime before);
    List<Visit> findByPatientProfileUserEmailIgnoreCaseAndAtBetweenOrderByAtAsc(String email, LocalDateTime from, LocalDateTime to);
}
