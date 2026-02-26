package com.passwordmanager.dto.request;

import lombok.Data;

@Data
public class PasswordGeneratorRequest {
    private int length = 16;
    private boolean uppercase = true;
    private boolean lowercase = true;
    private boolean numbers = true;
    private boolean specialChars = true;
    private boolean excludeSimilar = false;
    private int count = 1;
}
