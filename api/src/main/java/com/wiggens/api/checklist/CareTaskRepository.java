package com.wiggens.api.checklist;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CareTaskRepository extends JpaRepository<CareTask, Long> {
    List<CareTask> findByPatientProfileUserEmailIgnoreCaseAndActiveTrue(String email);
}

