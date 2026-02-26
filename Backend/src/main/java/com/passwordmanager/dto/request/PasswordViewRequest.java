package com.passwordmanager.dto.request;

import lombok.Data;

@Data
public class PasswordViewRequest {
    private String password;   // user re-enters login password
}