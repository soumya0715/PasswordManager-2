package com.passwordmanager.service;

import com.passwordmanager.dto.PasswordDTO;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PasswordServiceTest {

    @Mock
    private PasswordRepository passwordRepository;

    @InjectMocks
    private PasswordService passwordService;

    private User testUser;
    private Password testPassword;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@example.com");

        testPassword = new Password();
        testPassword.setId(1L);
        testPassword.setTitle("Gmail");
        testPassword.setUsername("testuser");
        testPassword.setPassword("encryptedPassword");
        testPassword.setUser(testUser);
        testPassword.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGetAllPasswords() {
        List<Password> passwords = new ArrayList<>();
        passwords.add(testPassword);

        when(passwordRepository.findByUserOrderByCreatedAtDesc(testUser)).thenReturn(passwords);

        List<PasswordDTO> result = passwordService.getAllPasswords(testUser);

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Gmail", result.get(0).getTitle());
    }

    @Test
    void testCreatePassword() {
        PasswordDTO dto = new PasswordDTO();
        dto.setTitle("Gmail");
        dto.setUsername("testuser");
        dto.setPassword("SecurePassword123!");

        when(passwordRepository.save(any())).thenReturn(testPassword);

        PasswordDTO result = passwordService.createPassword(dto, testUser);

        assertNotNull(result);
        assertEquals("Gmail", result.getTitle());
        assertTrue(result.getStrengthScore() > 0);
    }

    @Test
    void testToggleFavorite() {
        when(passwordRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testPassword));
        when(passwordRepository.save(any())).thenReturn(testPassword);

        PasswordDTO result = passwordService.toggleFavorite(1L, testUser);

        assertNotNull(result);
        verify(passwordRepository, times(1)).save(any());
    }

    @Test
    void testDeletePassword() {
        when(passwordRepository.findByIdAndUser(1L, testUser)).thenReturn(Optional.of(testPassword));

        passwordService.deletePassword(1L, testUser);

        verify(passwordRepository, times(1)).delete(testPassword);
    }

    @Test
    void testDeleteNonExistentPassword() {
        when(passwordRepository.findByIdAndUser(999L, testUser)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> passwordService.deletePassword(999L, testUser));
    }
}
