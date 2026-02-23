package com.passwordmanager.controller;

import com.passwordmanager.dto.request.PasswordGeneratorRequest;
import com.passwordmanager.dto.response.ApiResponse;
import com.passwordmanager.dto.response.PasswordGeneratorResponse;
import com.passwordmanager.service.PasswordGeneratorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/Password-generator")
@RequiredArgsConstructor
public class PasswordGeneratorController {

    private final PasswordGeneratorService passwordGeneratorService;

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<PasswordGeneratorResponse>> generate(
            @RequestBody PasswordGeneratorRequest request) {
        PasswordGeneratorResponse response = passwordGeneratorService.generate(request);
        return ResponseEntity.ok(ApiResponse.success(response, "Passwords generated"));
    }
}