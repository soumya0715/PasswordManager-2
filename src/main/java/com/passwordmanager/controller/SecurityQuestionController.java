package com.passwordmanager.controller;

import com.passwordmanager.dto.request.UpdateSecurityQuestionsRequest;
import com.passwordmanager.dto.response.ApiResponse;
import com.passwordmanager.entity.SecurityQuestion;
import com.passwordmanager.entity.User;
import com.passwordmanager.repository.UserRepository;
import com.passwordmanager.service.SecurityQuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.Map;

@RestController
@RequestMapping("/security-questions")
@RequiredArgsConstructor
public class SecurityQuestionController {

    private final SecurityQuestionService securityQuestionService;
    private final UserRepository userRepository;

    private User getUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/View")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> getQuestions(Authentication auth) {
        List<SecurityQuestion> questions = securityQuestionService.getQuestions(getUser(auth));
        // Return only question text, not answers
        List<Map<String, Object>> result = questions.stream()
                .map(q -> Map.of("id", (Object) q.getId(), "question", q.getQuestion()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(ApiResponse.success(result, "Questions fetched"));
    }

    @PostMapping("/Add")
    public ResponseEntity<ApiResponse<Void>> saveQuestions(
            Authentication auth, @RequestBody List<SecurityQuestion> questions) {
        securityQuestionService.saveQuestions(getUser(auth), questions);
        return ResponseEntity.ok(ApiResponse.success(null, "Security questions saved"));
    }
    @PostMapping("/Update")
    public ResponseEntity<ApiResponse<Void>> updateQuestions(
            Authentication auth,
            @RequestBody UpdateSecurityQuestionsRequest request) {

        User user = getUser(auth);

        securityQuestionService.updateAnswers(
                user,
                request.getPassword(),
                request.getQuestions()
        );

        return ResponseEntity.ok(
                ApiResponse.success(null, "Security answers updated successfully")
        );
    }
}
