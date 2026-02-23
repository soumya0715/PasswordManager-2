package com.passwordmanager.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class UpdateSecurityQuestionsRequest {

    private String password;   // login password
    private List<QuestionAnswerDTO> questions;

    @Data
    public static class QuestionAnswerDTO {
        private Long questionId;
        private String answer;
    }
}