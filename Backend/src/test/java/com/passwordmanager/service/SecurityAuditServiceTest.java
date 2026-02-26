package com.passwordmanager.service;

import com.passwordmanager.dto.response.SecurityAuditResponse;
import com.passwordmanager.entity.Password;
import com.passwordmanager.entity.User;
import com.passwordmanager.repository.PasswordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityAuditServiceTest {

    @Mock private PasswordRepository passwordRepository;
    @InjectMocks private SecurityAuditService service;

    private User user;

    @BeforeEach
    void setUp() { user = new User(); user.setId(1L); }

    @Test
    void generateReport_weakPassword_detected() {
        Password weak = new Password();
        weak.setId(1L); weak.setTitle("WeakEntry");
        weak.setPassword("abc"); weak.setStrengthScore(20);
        weak.setCreatedAt(LocalDateTime.now()); weak.setUpdatedAt(LocalDateTime.now());

        when(passwordRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(List.of(weak));
        SecurityAuditResponse report = service.generateReport(user);
        assertEquals(1, report.getWeakPasswords().size());
    }

    @Test
    void generateReport_reusedPasswords_detected() {
        Password p1 = new Password(); p1.setId(1L); p1.setTitle("Site1");
        p1.setPassword("SamePassword1!"); p1.setStrengthScore(70);
        p1.setCreatedAt(LocalDateTime.now()); p1.setUpdatedAt(LocalDateTime.now());

        Password p2 = new Password(); p2.setId(2L); p2.setTitle("Site2");
        p2.setPassword("SamePassword1!"); p2.setStrengthScore(70);
        p2.setCreatedAt(LocalDateTime.now()); p2.setUpdatedAt(LocalDateTime.now());

        when(passwordRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(Arrays.asList(p1, p2));
        SecurityAuditResponse report = service.generateReport(user);
        assertEquals(2, report.getReusedPasswords().size());
    }

    @Test
    void generateReport_emptyVault_returnsEmptyLists() {
        when(passwordRepository.findByUserOrderByCreatedAtDesc(user)).thenReturn(List.of());
        SecurityAuditResponse report = service.generateReport(user);
        assertTrue(report.getWeakPasswords().isEmpty());
        assertTrue(report.getReusedPasswords().isEmpty());
    }
}
