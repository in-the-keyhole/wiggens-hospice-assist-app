package com.wiggens.api.upload;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SecureUploadRepository extends JpaRepository<SecureUpload, Long> {
}

