package com.passwordmanager.service;

import com.passwordmanager.entity.User;
import com.passwordmanager.entity.VerificationCode;
import com.passwordmanager.repository.VerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class VerificationCodeService {

    private final VerificationCodeRepository verificationCodeRepository;

    public String generateCode(User user, String purpose) {
        verificationCodeRepository.deleteByUserAndPurpose(user, purpose);
        String code = String.format("%06d", new Random().nextInt(999999));
        VerificationCode vc = new VerificationCode();
        vc.setUser(user);
        vc.setCode(code);
        vc.setPurpose(purpose);
        vc.setIsUsed(false);
        vc.setExpiresAt(LocalDateTime.now().plusMinutes(10));
        verificationCodeRepository.save(vc);
        // Simulate email send - in real app send via SMTP
        System.out.println("=== VERIFICATION CODE [" + purpose + "] for " + user.getEmail() + ": " + code + " ===");
        return code;
    }

    public boolean verifyCode(User user, String code, String purpose) {
        VerificationCode vc = verificationCodeRepository
                .findByUserAndCodeAndPurposeAndIsUsedFalse(user, code, purpose)
                .orElse(null);
        if (vc == null) return false;
        if (vc.getExpiresAt().isBefore(LocalDateTime.now())) return false;
        vc.setIsUsed(true);
        verificationCodeRepository.save(vc);
        return true;
    }
}
