package com.passwordmanager.controller;

import com.passwordmanager.dto.PasswordDTO;
import com.passwordmanager.dto.request.PasswordViewRequest;
import com.passwordmanager.dto.response.ApiResponse;
import com.passwordmanager.entity.User;
import com.passwordmanager.repository.UserRepository;
import com.passwordmanager.service.PasswordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/passwords")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;
    private final UserRepository userRepository;

    private User getAuthenticatedUser(Authentication auth) {
        String email = auth.getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping
    public ResponseEntity<List<PasswordDTO>> getAllPasswords(Authentication auth) {
        User user = getAuthenticatedUser(auth);
        List<PasswordDTO> passwords = passwordService.getAllPasswords(user);
        return ResponseEntity.ok(passwords);
    }

    @GetMapping("/favorites")
    public ResponseEntity<List<PasswordDTO>> getFavoritePasswords(Authentication auth) {
        User user = getAuthenticatedUser(auth);
        List<PasswordDTO> passwords = passwordService.getFavoritePasswords(user);
        return ResponseEntity.ok(passwords);
    }

    @PostMapping("/{id}/view")
    public ResponseEntity<ApiResponse<PasswordDTO>> viewPassword(
            @PathVariable Long id,
            @RequestBody PasswordViewRequest request,
            Authentication auth) {

        User user = getAuthenticatedUser(auth);

        PasswordDTO dto = passwordService.viewPassword(id, user, request.getPassword());

        return ResponseEntity.ok(
                ApiResponse.success(dto, "Password verified")
        );
    }
    @PostMapping
    public ResponseEntity<PasswordDTO> createPassword(@Valid @RequestBody PasswordDTO dto, Authentication auth) {
        User user = getAuthenticatedUser(auth);
        PasswordDTO password = passwordService.createPassword(dto, user);
        return ResponseEntity.status(HttpStatus.CREATED).body(password);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PasswordDTO> updatePassword(@PathVariable Long id, @Valid @RequestBody PasswordDTO dto, Authentication auth) {
        User user = getAuthenticatedUser(auth);
        PasswordDTO password = passwordService.updatePassword(id, dto, user);
        return ResponseEntity.ok(password);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePassword(@PathVariable Long id, Authentication auth) {
        User user = getAuthenticatedUser(auth);
        passwordService.deletePassword(id, user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/favourite")
    public ResponseEntity<PasswordDTO> toggleFavorite(@PathVariable Long id, Authentication auth) {
        User user = getAuthenticatedUser(auth);
        PasswordDTO password = passwordService.toggleFavorite(id, user);
        return ResponseEntity.ok(password);
    }

    @GetMapping("/search")
    public ResponseEntity<List<PasswordDTO>> searchPasswords(@RequestParam String keyword, Authentication auth) {
        User user = getAuthenticatedUser(auth);
        List<PasswordDTO> passwords = passwordService.searchPasswords(keyword, user);
        return ResponseEntity.ok(passwords);
    }
}
