package com.passwordmanager.service;

import com.passwordmanager.dto.request.PasswordGeneratorRequest;
import com.passwordmanager.dto.response.PasswordGeneratorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordGeneratorServiceTest {

    private PasswordGeneratorService service;

    @BeforeEach
    void setUp() { service = new PasswordGeneratorService(); }

    @Test
    void generatePassword_defaultOptions_returnsPassword() {
        PasswordGeneratorRequest req = new PasswordGeneratorRequest();
        req.setLength(16); req.setUppercase(true); req.setLowercase(true);
        req.setNumbers(true); req.setSpecialChars(true); req.setCount(1);
        PasswordGeneratorResponse res = service.generate(req);
        assertNotNull(res);
        assertEquals(1, res.getPasswords().size());
        assertEquals(16, res.getPasswords().get(0).length());
    }

    @Test
    void generatePassword_lengthConstraint_between8And64() {
        PasswordGeneratorRequest req = new PasswordGeneratorRequest();
        req.setLength(5); req.setLowercase(true); req.setCount(1);
        PasswordGeneratorResponse res = service.generate(req);
        assertTrue(res.getPasswords().get(0).length() >= 8);

        req.setLength(100);
        res = service.generate(req);
        assertTrue(res.getPasswords().get(0).length() <= 64);
    }

    @Test
    void generateMultiplePasswords_returnsCorrectCount() {
        PasswordGeneratorRequest req = new PasswordGeneratorRequest();
        req.setLength(12); req.setLowercase(true); req.setCount(5);
        PasswordGeneratorResponse res = service.generate(req);
        assertEquals(5, res.getPasswords().size());
    }

    @Test
    void generatePassword_strengthScoreNotZero() {
        PasswordGeneratorRequest req = new PasswordGeneratorRequest();
        req.setLength(16); req.setUppercase(true); req.setLowercase(true);
        req.setNumbers(true); req.setSpecialChars(true); req.setCount(1);
        PasswordGeneratorResponse res = service.generate(req);
        assertTrue(res.getStrengthScore() > 0);
    }
}
