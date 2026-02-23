package com.passwordmanager.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class PasswordRecoveryRequest {
    private String email;
    private List<SecurityAnswerDTO> answers;
    private String newPassword;

    @Data
    public static class SecurityAnswerDTO {
        private Long questionId;
        private String answer;
    }
}
