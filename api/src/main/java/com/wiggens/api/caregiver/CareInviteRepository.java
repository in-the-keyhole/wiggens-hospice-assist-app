package com.wiggens.api.caregiver;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CareInviteRepository extends JpaRepository<CareInvite, Long> {
    Optional<CareInvite> findByToken(String token);
}

