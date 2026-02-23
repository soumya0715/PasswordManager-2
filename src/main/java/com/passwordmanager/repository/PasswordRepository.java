package com.passwordmanager.repository;

import com.passwordmanager.entity.Password;
import com.passwordmanager.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PasswordRepository extends JpaRepository<Password, Long> {
    List<Password> findByUserOrderByCreatedAtDesc(User user);
    List<Password> findByUserAndIsFavoriteTrue(User user);
    Optional<Password> findByIdAndUser(Long id, User user);
    
    @Query("SELECT p FROM Password p WHERE p.user = :user AND p.title LIKE %:keyword%")
    List<Password> searchByTitle(@Param("user") User user, @Param("keyword") String keyword);
}
