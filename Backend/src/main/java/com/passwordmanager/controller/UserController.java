package com.passwordmanager.controller;

import com.passwordmanager.dto.request.ChangePasswordRequest;
import com.passwordmanager.dto.request.TwoFactorRequest;
import com.passwordmanager.dto.request.VerifyMasterPasswordRequest;
import com.passwordmanager.dto.response.ApiResponse;
import com.passwordmanager.dto.response.UserProfileResponse;
import com.passwordmanager.entity.User;
import com.passwordmanager.repository.UserRepository;
import com.passwordmanager.service.TwoFactorAuthService;
import com.passwordmanager.service.UserService;
import com.passwordmanager.service.VerificationCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final TwoFactorAuthService twoFactorAuthService;
    private final VerificationCodeService verificationCodeService;

    private User getUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> getProfile(Authentication auth) {
        UserProfileResponse profile = userService.getProfile(getUser(auth));
        return ResponseEntity.ok(ApiResponse.success(profile, "Profile fetched"));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<UserProfileResponse>> updateProfile(
            Authentication auth, @RequestBody Map<String, String> body) {
        User user = getUser(auth);
        UserProfileResponse profile = userService.updateProfile(
                user, body.get("firstName"), body.get("lastName"), body.get("phone"));
        return ResponseEntity.ok(ApiResponse.success(profile, "Profile updated"));
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            Authentication auth, @RequestBody ChangePasswordRequest request) {
        userService.changeMasterPassword(getUser(auth), request.getCurrentPassword(), request.getNewPassword());
        return ResponseEntity.ok(ApiResponse.success(null, "Password changed successfully"));
    }

    @PostMapping("/verify-master-password")
    public ResponseEntity<ApiResponse<Boolean>> verifyMasterPassword(
            Authentication auth, @RequestBody VerifyMasterPasswordRequest request) {
        boolean valid = userService.verifyMasterPassword(getUser(auth), request.getMasterPassword());
        return ResponseEntity.ok(ApiResponse.success(valid, valid ? "Valid" : "Invalid"));
    }

    @PostMapping("/2fa/enable")
    public ResponseEntity<ApiResponse<String>> enable2FA(Authentication auth) {
        String secret = twoFactorAuthService.enableTwoFactor(getUser(auth));
        return ResponseEntity.ok(ApiResponse.success(secret, "2FA enabled. Check console for code."));
    }

    @PostMapping("/2fa/disable")
    public ResponseEntity<ApiResponse<Void>> disable2FA(Authentication auth) {
        twoFactorAuthService.disableTwoFactor(getUser(auth));
        return ResponseEntity.ok(ApiResponse.success(null, "2FA disabled"));
    }

    @PostMapping("/send-verification-code")
    public ResponseEntity<ApiResponse<String>> sendVerificationCode(Authentication auth) {
        User user = getUser(auth);
        String code = verificationCodeService.generateCode(user, "SENSITIVE_OP");
        return ResponseEntity.ok(ApiResponse.success("sent", "Verification code sent to " + user.getEmail()));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<ApiResponse<Boolean>> verifyCode(
            Authentication auth, @RequestBody Map<String, String> body) {
        boolean valid = verificationCodeService.verifyCode(getUser(auth), body.get("code"), "SENSITIVE_OP");
        return ResponseEntity.ok(ApiResponse.success(valid, valid ? "Valid" : "Invalid or expired"));
    }
}
