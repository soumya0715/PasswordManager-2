package com.passwordmanager.controller;

import com.passwordmanager.dto.response.ApiResponse;
import com.passwordmanager.dto.response.SecurityAuditResponse;
import com.passwordmanager.entity.User;
import com.passwordmanager.repository.UserRepository;
import com.passwordmanager.service.SecurityAuditService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/security")
@RequiredArgsConstructor
public class SecurityAuditController {

    private final SecurityAuditService securityAuditService;
    private final UserRepository userRepository;

    @GetMapping("/audit")
    public ResponseEntity<ApiResponse<SecurityAuditResponse>> getAuditReport(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        SecurityAuditResponse report = securityAuditService.generateReport(user);
        return ResponseEntity.ok(ApiResponse.success(report, "Audit report generated"));
    }
}
