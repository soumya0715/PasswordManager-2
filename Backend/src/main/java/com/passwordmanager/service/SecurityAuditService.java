package com.passwordmanager.service;

import com.passwordmanager.dto.response.SecurityAuditResponse;
import com.passwordmanager.entity.Password;
import com.passwordmanager.entity.User;
import com.passwordmanager.repository.PasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SecurityAuditService {

    private final PasswordRepository passwordRepository;

    public SecurityAuditResponse generateReport(User user) {
        List<Password> allPasswords = passwordRepository.findByUserOrderByCreatedAtDesc(user);
        SecurityAuditResponse response = new SecurityAuditResponse();

        // Weak passwords (score < 40)
        List<SecurityAuditResponse.AuditItem> weak = allPasswords.stream()
                .filter(p -> p.getStrengthScore() != null && p.getStrengthScore() < 40)
                .map(p -> {
                    SecurityAuditResponse.AuditItem item = new SecurityAuditResponse.AuditItem();
                    item.setId(p.getId());
                    item.setTitle(p.getTitle());
                    item.setIssue("Weak password");
                    item.setStrengthScore(p.getStrengthScore());
                    return item;
                }).collect(Collectors.toList());

        // Reused passwords
        Map<String, List<Password>> grouped = allPasswords.stream()
                .collect(Collectors.groupingBy(Password::getPassword));
        List<SecurityAuditResponse.AuditItem> reused = grouped.entrySet().stream()
                .filter(e -> e.getValue().size() > 1)
                .flatMap(e -> e.getValue().stream())
                .map(p -> {
                    SecurityAuditResponse.AuditItem item = new SecurityAuditResponse.AuditItem();
                    item.setId(p.getId());
                    item.setTitle(p.getTitle());
                    item.setIssue("Password reused");
                    item.setStrengthScore(p.getStrengthScore() != null ? p.getStrengthScore() : 0);
                    return item;
                }).collect(Collectors.toList());

        // Old passwords (> 90 days)
        LocalDateTime threshold = LocalDateTime.now().minusDays(90);
        List<SecurityAuditResponse.AuditItem> old = allPasswords.stream()
                .filter(p -> p.getUpdatedAt() != null && p.getUpdatedAt().isBefore(threshold))
                .map(p -> {
                    SecurityAuditResponse.AuditItem item = new SecurityAuditResponse.AuditItem();
                    item.setId(p.getId());
                    item.setTitle(p.getTitle());
                    item.setIssue("Password not changed in 90+ days");
                    item.setStrengthScore(p.getStrengthScore() != null ? p.getStrengthScore() : 0);
                    return item;
                }).collect(Collectors.toList());

        response.setWeakPasswords(weak);
        response.setReusedPasswords(reused);
        response.setOldPasswords(old);

        int issues = weak.size() + reused.size() + old.size();
        int total = allPasswords.isEmpty() ? 1 : allPasswords.size();
        response.setOverallScore(Math.max(0, 100 - (issues * 10 / total * 10)));
        return response;
    }
}
