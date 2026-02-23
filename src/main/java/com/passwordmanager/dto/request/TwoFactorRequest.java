package com.passwordmanager.dto.request;

import lombok.Data;

@Data
public class TwoFactorRequest {
    private String code;
    private String method;
}
