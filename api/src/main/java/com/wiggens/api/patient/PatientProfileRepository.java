package com.wiggens.api.patient;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PatientProfileRepository extends JpaRepository<PatientProfile, Long> {
    Optional<PatientProfile> findByUserEmailIgnoreCase(String email);
}

