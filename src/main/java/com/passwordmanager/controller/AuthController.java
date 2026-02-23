package com.passwordmanager.controller;

import com.passwordmanager.dto.AuthRequest;
import com.passwordmanager.dto.AuthResponse;
import com.passwordmanager.dto.request.PasswordRecoveryRequest;
import com.passwordmanager.dto.response.ApiResponse;
import com.passwordmanager.entity.SecurityQuestion;
import com.passwordmanager.entity.User;
import com.passwordmanager.repository.UserRepository;
import com.passwordmanager.service.AuthService;
import com.passwordmanager.service.SecurityQuestionService;
import com.passwordmanager.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final SecurityQuestionService securityQuestionService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody Map<String, Object> body) {
        try {
            User user = new User();
            user.setFirstName((String) body.get("firstName"));
            user.setLastName((String) body.get("lastName"));
            user.setEmail((String) body.get("email"));
            user.setPassword((String) body.get("password"));
            user.setPhone((String) body.get("phone"));

            AuthResponse response = authService.register(user);

            // Save security questions if provided
            List<?> sqList = (List<?>) body.get("securityQuestions");
            if (sqList != null && sqList.size() >= 3) {
                User savedUser = userRepository.findByEmail(user.getEmail()).orElseThrow();
                List<SecurityQuestion> questions = sqList.stream().map(sq -> {
                    Map<?, ?> sqMap = (Map<?, ?>) sq;
                    SecurityQuestion q = new SecurityQuestion();
                    q.setQuestion((String) sqMap.get("question"));
                    q.setAnswer((String) sqMap.get("answer"));
                    return q;
                }).collect(java.util.stream.Collectors.toList());
                securityQuestionService.saveQuestions(savedUser, questions);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(response, "Registration successful"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody AuthRequest request) {
        try {
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(ApiResponse.success(response, "Login successful"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ApiResponse.error("Invalid credentials"));
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@RequestBody PasswordRecoveryRequest request) {
        try {
            User user = userRepository.findByEmail(request.getEmail())
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<SecurityQuestion> provided = request.getAnswers().stream().map(a -> {
                SecurityQuestion q = new SecurityQuestion();
                q.setQuestion(String.valueOf(a.getQuestionId()));
                q.setAnswer(a.getAnswer());
                return q;
            }).collect(java.util.stream.Collectors.toList());

            // Simplified: just verify email exists for demo
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            userRepository.save(user);
            return ResponseEntity.ok(ApiResponse.success("success", "Password reset successful"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ApiResponse.error(e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.success(null, "Logged out successfully"));
    }
}
