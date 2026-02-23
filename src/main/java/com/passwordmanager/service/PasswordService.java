package com.passwordmanager.service;

import com.passwordmanager.dto.PasswordDTO;
import com.passwordmanager.entity.Category;
import com.passwordmanager.entity.Password;
import com.passwordmanager.entity.User;
import com.passwordmanager.repository.CategoryRepository;
import com.passwordmanager.repository.PasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final PasswordRepository passwordRepository;

    private final CategoryRepository categoryRepository;

    public List<PasswordDTO> getAllPasswords(User user) {
        return passwordRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<PasswordDTO> getFavoritePasswords(User user) {
        return passwordRepository.findByUserAndIsFavoriteTrue(user)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public PasswordDTO getPassword(Long id, User user) {
        Password password = passwordRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Password not found"));
        return convertToDTO(password);
    }

    public PasswordDTO createPassword(PasswordDTO dto, User user) {

        Password password = new Password();
        password.setTitle(dto.getTitle());
        password.setUsername(dto.getUsername());
        password.setPassword(dto.getPassword());
        password.setEmail(dto.getEmail());
        password.setUrl(dto.getUrl());
        password.setNotes(dto.getNotes());
        password.setUser(user);
        password.setIsFavorite(false);
        password.setStrengthScore(calculateStrength(dto.getPassword()));

        // ✅ CATEGORY LOGIC
        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findByIdAndUser(dto.getCategoryId(), user)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            password.setCategory(category);
        }

        Password saved = passwordRepository.save(password);
        return convertToDTO(saved);
    }

    public PasswordDTO updatePassword(Long id, PasswordDTO dto, User user) {
        Password password = passwordRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Password not found"));

        password.setTitle(dto.getTitle());
        password.setUsername(dto.getUsername());
        password.setPassword(dto.getPassword());
        password.setEmail(dto.getEmail());
        password.setUrl(dto.getUrl());
        password.setNotes(dto.getNotes());
        password.setStrengthScore(calculateStrength(dto.getPassword()));

        if (dto.getCategoryId() != null) {
            Category category = categoryRepository.findByIdAndUser(dto.getCategoryId(), user)
                    .orElseThrow(() -> new RuntimeException("Category not found"));
            password.setCategory(category);
        }
        Password updated = passwordRepository.save(password);
        return convertToDTO(updated);
    }

    public void deletePassword(Long id, User user) {
        Password password = passwordRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Password not found"));
        passwordRepository.delete(password);
    }

    public PasswordDTO toggleFavorite(Long id, User user) {
        Password password = passwordRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new RuntimeException("Password not found"));
        password.setIsFavorite(!password.getIsFavorite());
        Password updated = passwordRepository.save(password);
        return convertToDTO(updated);
    }

    public List<PasswordDTO> searchPasswords(String keyword, User user) {

        return passwordRepository.smartSearch(user, keyword)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PasswordDTO convertToDTO(Password password) {
        PasswordDTO dto = new PasswordDTO();
        dto.setId(password.getId());
        dto.setTitle(password.getTitle());
        dto.setUsername(password.getUsername());
        dto.setPassword(password.getPassword());
        dto.setEmail(password.getEmail());
        dto.setUrl(password.getUrl());
        dto.setNotes(password.getNotes());
        dto.setStrengthScore(password.getStrengthScore());
        dto.setIsFavorite(password.getIsFavorite());
        if (password.getCategory() != null) {
            dto.setCategoryId(password.getCategory().getId());
            dto.setCategoryName(password.getCategory().getName());
        }
        dto.setCreatedAt(password.getCreatedAt());
        dto.setUpdatedAt(password.getUpdatedAt());
        return dto;
    }

    private Integer calculateStrength(String password) {
        int score = 0;
        if (password.length() >= 8) score += 20;
        if (password.length() >= 12) score += 10;
        if (password.matches(".*[a-z].*")) score += 10;
        if (password.matches(".*[A-Z].*")) score += 10;
        if (password.matches(".*[0-9].*")) score += 20;
        if (password.matches(".*[!@#$%^&*].*")) score += 30;
        return Math.min(score, 100);
    }
}
