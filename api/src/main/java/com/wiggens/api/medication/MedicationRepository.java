package com.wiggens.api.medication;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicationRepository extends JpaRepository<Medication, Long> {
    List<Medication> findByPatientProfileUserEmailIgnoreCaseAndActiveTrue(String email);
}

