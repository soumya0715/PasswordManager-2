package com.passwordmanager.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class PasswordGeneratorResponse {
    private List<String> passwords;
    private int strengthScore;
    private String strengthLabel;
}
