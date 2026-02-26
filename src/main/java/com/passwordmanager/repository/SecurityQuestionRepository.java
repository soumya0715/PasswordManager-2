package com.passwordmanager.repository;

import com.passwordmanager.entity.SecurityQuestion;
import com.passwordmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface SecurityQuestionRepository extends JpaRepository<SecurityQuestion, Long> {
    List<SecurityQuestion> findByUser(User user);
    long countByUser(User user);
}
