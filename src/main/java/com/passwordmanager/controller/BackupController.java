package com.passwordmanager.controller;

import com.passwordmanager.dto.response.ApiResponse;
import com.passwordmanager.entity.User;
import com.passwordmanager.repository.UserRepository;
import com.passwordmanager.service.BackupService;
import com.passwordmanager.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/backup")
@RequiredArgsConstructor
public class BackupController {

    private final BackupService backupService;
    private final PasswordService passwordService;
    private final UserRepository userRepository;

    private User getUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> exportVault(Authentication auth) throws Exception {
        byte[] data = backupService.exportVault(getUser(auth));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vault-backup.enc")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(data);
    }

    @PostMapping("/import")
    public ResponseEntity<ApiResponse<String>> importVault(
            Authentication auth, @RequestParam("file") MultipartFile file) throws Exception {
        int count = backupService.importVault(getUser(auth), file.getBytes(), passwordService);
        return ResponseEntity.ok(ApiResponse.success("Imported " + count + " passwords", "Import successful"));
    }
}
