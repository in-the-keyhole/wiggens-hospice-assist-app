package com.wiggens.api.upload;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "secure_uploads")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SecureUpload {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String originalFileName;
    private String contentType;
    private String storagePath;
    private String ivBase64;
    private String ownerEmail;

    private Instant createdAt;
}

