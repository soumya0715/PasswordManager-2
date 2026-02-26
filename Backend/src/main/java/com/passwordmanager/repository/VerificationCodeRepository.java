package com.passwordmanager.repository;

import com.passwordmanager.entity.User;
import com.passwordmanager.entity.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByUserAndCodeAndPurposeAndIsUsedFalse(User user, String code, String purpose);
    void deleteByUserAndPurpose(User user, String purpose);
}
