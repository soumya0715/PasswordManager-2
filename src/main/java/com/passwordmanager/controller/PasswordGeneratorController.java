package com.passwordmanager.controller;

import com.passwordmanager.dto.PasswordDTO;
import com.passwordmanager.dto.request.PasswordGeneratorRequest;
import com.passwordmanager.dto.response.ApiResponse;
import com.passwordmanager.dto.response.PasswordGeneratorResponse;
import com.passwordmanager.entity.User;
import com.passwordmanager.repository.UserRepository;
import com.passwordmanager.service.PasswordGeneratorService;
import com.passwordmanager.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Password-generator")
@RequiredArgsConstructor
public class PasswordGeneratorController {

    private final PasswordGeneratorService passwordGeneratorService;
    private final PasswordService passwordService;
    private final UserRepository userRepository;

    private User getUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<PasswordGeneratorResponse>> generate(
            @RequestBody PasswordGeneratorRequest request) {

        PasswordGeneratorResponse response =
                passwordGeneratorService.generate(request);

        return ResponseEntity.ok(ApiResponse.success(response, "Passwords generated"));
    }

    @PostMapping("/generate-and-save")
    public ResponseEntity<ApiResponse<String>> generateAndSave(
            Authentication auth,
            @RequestBody PasswordGeneratorRequest request) {

        User user = getUser(auth);

        PasswordGeneratorResponse response =
                passwordGeneratorService.generate(request);

        String generatedPassword = response.getPasswords().get(0);

        PasswordDTO dto = new PasswordDTO();
        dto.setTitle("Generated Password");
        dto.setPassword(generatedPassword);

        passwordService.createPassword(dto, user);

        return ResponseEntity.ok(
                ApiResponse.success("Password saved to vault", "Success")
        );
    }
}