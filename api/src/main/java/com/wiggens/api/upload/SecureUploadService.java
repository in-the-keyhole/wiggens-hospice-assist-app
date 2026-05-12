package com.wiggens.api.upload;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class SecureUploadService {
    private final SecureUploadRepository repository;

    @Value("${uploads.dir:#{systemProperties['java.io.tmpdir']}/wiggens-uploads}")
    private String uploadDir;

    // 256-bit key represented as hex or base64. For dev default to a fixed key for determinism.
    @Value("${uploads.encryptionKey:ZmFrZS1kZXYta2V5LTIzMi1ieXRlcy1sZW5ndGg=}")
    private String encryptionKeyBase64;

    private SecretKey secretKey;

    @PostConstruct
    void init() throws Exception {
        Path dir = Path.of(uploadDir);
        Files.createDirectories(dir);
        byte[] keyBytes = Base64.getDecoder().decode(encryptionKeyBase64);
        if (keyBytes.length != 16 && keyBytes.length != 32) {
            // fallback: derive 256-bit key from provided bytes via padding/truncation
            byte[] padded = new byte[32];
            System.arraycopy(keyBytes, 0, padded, 0, Math.min(keyBytes.length, 32));
            keyBytes = padded;
        }
        secretKey = new SecretKeySpec(keyBytes, "AES");
    }

    public SecureUpload save(MultipartFile file, String ownerEmail) throws Exception {
        byte[] plaintext = file.getBytes();
        byte[] iv = new byte[12];
        new SecureRandom().nextBytes(iv);

        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));
        byte[] ciphertext = cipher.doFinal(plaintext);

        String filename = System.currentTimeMillis() + "_" + sanitize(file.getOriginalFilename());
        Path out = Path.of(uploadDir).resolve(filename);
        Files.write(out, ciphertext);

        SecureUpload rec = SecureUpload.builder()
                .originalFileName(file.getOriginalFilename())
                .contentType(file.getContentType())
                .storagePath(out.toAbsolutePath().toString())
                .ivBase64(Base64.getEncoder().encodeToString(iv))
                .ownerEmail(ownerEmail)
                .createdAt(Instant.now())
                .build();
        return repository.save(rec);
    }

    public byte[] loadDecrypted(Long id) throws Exception {
        var rec = repository.findById(id).orElseThrow();
        byte[] ciphertext = Files.readAllBytes(Path.of(rec.getStoragePath()));
        byte[] iv = Base64.getDecoder().decode(rec.getIvBase64());
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(128, iv));
        return cipher.doFinal(ciphertext);
    }

    public String contentType(Long id) {
        return repository.findById(id).map(SecureUpload::getContentType).orElse("application/octet-stream");
    }

    private String sanitize(String name) {
        if (name == null) return "file";
        return name.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}

