package com.passwordmanager.service;

import com.passwordmanager.dto.request.PasswordGeneratorRequest;
import com.passwordmanager.dto.response.PasswordGeneratorResponse;
import org.springframework.stereotype.Service;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

@Service
public class PasswordGeneratorService {

    private static final String UPPERCASE = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String UPPERCASE_SIMILAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWERCASE = "abcdefghjkmnpqrstuvwxyz";
    private static final String LOWERCASE_SIMILAR = "abcdefghijklmnopqrstuvwxyz";
    private static final String NUMBERS = "23456789";
    private static final String NUMBERS_SIMILAR = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()_+-=[]{}|;:,.<>?";

    public PasswordGeneratorResponse generate(PasswordGeneratorRequest request) {
        List<String> passwords = new ArrayList<>();
        int count = Math.min(request.getCount(), 10);
        for (int i = 0; i < count; i++) {
            passwords.add(generateSingle(request));
        }
        String sample = passwords.get(0);
        int strength = calculateStrength(sample);
        String label = strength >= 80 ? "Very Strong" : strength >= 60 ? "Strong" : strength >= 40 ? "Medium" : "Weak";

        PasswordGeneratorResponse response = new PasswordGeneratorResponse();
        response.setPasswords(passwords);
        response.setStrengthScore(strength);
        response.setStrengthLabel(label);
        return response;
    }

    private String generateSingle(PasswordGeneratorRequest req) {
        StringBuilder charset = new StringBuilder();
        if (req.isUppercase()) charset.append(req.isExcludeSimilar() ? UPPERCASE : UPPERCASE_SIMILAR);
        if (req.isLowercase()) charset.append(req.isExcludeSimilar() ? LOWERCASE : LOWERCASE_SIMILAR);
        if (req.isNumbers()) charset.append(req.isExcludeSimilar() ? NUMBERS : NUMBERS_SIMILAR);
        if (req.isSpecialChars()) charset.append(SPECIAL);
        if (charset.length() == 0) charset.append(LOWERCASE_SIMILAR);

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();
        int length = Math.max(8, Math.min(req.getLength(), 64));
        for (int i = 0; i < length; i++) {
            password.append(charset.charAt(random.nextInt(charset.length())));
        }
        return password.toString();
    }

    private int calculateStrength(String password) {
        int score = 0;
        if (password.length() >= 8) score += 20;
        if (password.length() >= 12) score += 10;
        if (password.length() >= 16) score += 10;
        if (password.matches(".*[a-z].*")) score += 10;
        if (password.matches(".*[A-Z].*")) score += 10;
        if (password.matches(".*[0-9].*")) score += 15;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{}|;:,.<>?].*")) score += 25;
        return Math.min(score, 100);
    }
}
