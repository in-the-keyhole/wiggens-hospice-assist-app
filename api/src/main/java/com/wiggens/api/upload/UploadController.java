package com.wiggens.api.upload;

import com.wiggens.api.upload.dto.UploadResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/codex-example/api/v1/uploads")
@RequiredArgsConstructor
public class UploadController {
    private final SecureUploadService service;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UploadResponse> upload(@AuthenticationPrincipal UserDetails user,
                                                 @RequestPart("file") MultipartFile file) throws Exception {
        var saved = service.save(file, user.getUsername());
        return ResponseEntity.ok(UploadResponse.builder()
                .id(saved.getId())
                .url("/codex-example/api/v1/uploads/" + saved.getId())
                .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<byte[]> download(@PathVariable Long id) throws Exception {
        byte[] bytes = service.loadDecrypted(id);
        String contentType = service.contentType(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "private, max-age=0, no-store")
                .contentType(contentType != null ? MediaType.parseMediaType(contentType) : MediaType.APPLICATION_OCTET_STREAM)
                .body(bytes);
    }
}

