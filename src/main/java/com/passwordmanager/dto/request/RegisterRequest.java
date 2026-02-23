package com.passwordmanager.dto.request;

import lombok.Data;
import java.util.List;

@Data
public class RegisterRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phone;
    private List<SecurityQuestionDTO> securityQuestions;

    @Data
    public static class SecurityQuestionDTO {
        private String question;
        private String answer;
    }
}
