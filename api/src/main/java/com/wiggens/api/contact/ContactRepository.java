package com.wiggens.api.contact;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {
    List<Contact> findByPatientProfileUserEmailIgnoreCaseOrderByNameAsc(String email);
}

