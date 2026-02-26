package com.passwordmanager.service;

import com.passwordmanager.dto.request.UpdateSecurityQuestionsRequest;
import com.passwordmanager.entity.SecurityQuestion;
import com.passwordmanager.entity.User;
import com.passwordmanager.repository.SecurityQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityQuestionService {

    private final SecurityQuestionRepository securityQuestionRepository;
    private final PasswordEncoder passwordEncoder;

    public List<SecurityQuestion> getQuestions(User user) {
        return securityQuestionRepository.findByUser(user);
    }

    public void saveQuestions(User user, List<SecurityQuestion> questions) {
        if (questions.size() < 3) throw new RuntimeException("Minimum 3 security questions required");
        for (SecurityQuestion q : questions) {
            q.setUser(user);
            q.setAnswer(passwordEncoder.encode(q.getAnswer().toLowerCase().trim()));
        }
        securityQuestionRepository.saveAll(questions);
    }

    public boolean verifyAnswers(User user, List<SecurityQuestion> provided) {
        List<SecurityQuestion> stored = securityQuestionRepository.findByUser(user);
        if (stored.size() < 3) return false;
        int correct = 0;
        for (SecurityQuestion storedQ : stored) {
            for (SecurityQuestion providedQ : provided) {
                if (storedQ.getQuestion().equals(providedQ.getQuestion()) &&
                    passwordEncoder.matches(providedQ.getAnswer().toLowerCase().trim(), storedQ.getAnswer())) {
                    correct++;
                }
            }
        }
        return correct >= 3;
    }

    public void updateAnswers(User user,
                              String password,
                              List<UpdateSecurityQuestionsRequest.QuestionAnswerDTO> updates) {

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        List<SecurityQuestion> storedQuestions =
                securityQuestionRepository.findByUser(user);

        for (SecurityQuestion stored : storedQuestions) {

            for (UpdateSecurityQuestionsRequest.QuestionAnswerDTO update : updates) {

                if (stored.getId().equals(update.getQuestionId())) {

                    stored.setAnswer(
                            passwordEncoder.encode(
                                    update.getAnswer().toLowerCase().trim()
                            )
                    );
                }
            }
        }

        // 4️⃣ Save updated answers
        securityQuestionRepository.saveAll(storedQuestions);
    }
}
