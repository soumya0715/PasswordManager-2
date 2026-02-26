package com.passwordmanager.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.passwordmanager.dto.PasswordDTO;
import com.passwordmanager.entity.User;
import com.passwordmanager.repository.PasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BackupService {

    private final PasswordRepository passwordRepository;
    private final EncryptionService encryptionService;
    private final ObjectMapper objectMapper;

    public byte[] exportVault(User user) throws Exception {
        List<PasswordDTO> passwords = passwordRepository.findByUserOrderByCreatedAtDesc(user)
                .stream()
                .map(p -> {
                    PasswordDTO dto = new PasswordDTO();
                    dto.setId(p.getId());
                    dto.setTitle(p.getTitle());
                    dto.setUsername(p.getUsername());
                    dto.setPassword(p.getPassword());
                    dto.setEmail(p.getEmail());
                    dto.setUrl(p.getUrl());
                    dto.setNotes(p.getNotes());
                    return dto;
                }).collect(Collectors.toList());

        String json = objectMapper.writeValueAsString(passwords);
        String encrypted = encryptionService.encrypt(json);
        return encrypted.getBytes();
    }

    public int importVault(User user, byte[] data, PasswordService passwordService) throws Exception {
        String encrypted = new String(data);
        String json = encryptionService.decrypt(encrypted);
        List<PasswordDTO> passwords = objectMapper.readValue(json,
                objectMapper.getTypeFactory().constructCollectionType(List.class, PasswordDTO.class));
        for (PasswordDTO dto : passwords) {
            try {
                passwordService.createPassword(dto, user);
            } catch (Exception ignored) {}
        }
        return passwords.size();
    }
}
