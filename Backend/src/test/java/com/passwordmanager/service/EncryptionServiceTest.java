package com.passwordmanager.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EncryptionServiceTest {

    private EncryptionService service;

    @BeforeEach
    void setUp() { service = new EncryptionService(); }

    @Test
    void encrypt_thenDecrypt_returnsOriginal() {
        String original = "MySecretPassword123!";
        String encrypted = service.encrypt(original);
        String decrypted = service.decrypt(encrypted);
        assertEquals(original, decrypted);
    }

    @Test
    void encrypt_differentFromOriginal() {
        String original = "TestPassword";
        String encrypted = service.encrypt(original);
        assertNotEquals(original, encrypted);
    }

    @Test
    void encrypt_sameInput_sameOutput() {
        String data = "ConsistentData";
        assertEquals(service.encrypt(data), service.encrypt(data));
    }
}
