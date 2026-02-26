package com.passwordmanager.service;

import com.passwordmanager.entity.User;
import com.passwordmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TwoFactorAuthService {

    private final UserRepository userRepository;
    private final VerificationCodeService verificationCodeService;

    public String enableTwoFactor(User user) {
        String secret = UUID.randomUUID().toString().replace("-", "").substring(0, 16);
        user.setTwoFASecret(secret);
        user.setTwoFAEnabled(true);
        user.setTwoFAMethod("TOTP");
        userRepository.save(user);
        // Send simulated 2FA code
        verificationCodeService.generateCode(user, "2FA_SETUP");
        return secret;
    }

    public void disableTwoFactor(User user) {
        user.setTwoFAEnabled(false);
        user.setTwoFASecret(null);
        user.setTwoFAMethod(null);
        userRepository.save(user);
    }

    public boolean verifyTwoFactorCode(User user, String code) {
        return verificationCodeService.verifyCode(user, code, "2FA_SETUP") ||
               verificationCodeService.verifyCode(user, code, "2FA_LOGIN");
    }
}
