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

    @Query("""
SELECT p FROM Password p
WHERE p.user = :user
AND (
    LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR LOWER(p.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR LOWER(p.url) LIKE LOWER(CONCAT('%', :keyword, '%'))
    OR LOWER(p.category.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
)
""")
    List<Password> smartSearch(@Param("user") User user,
                               @Param("keyword") String keyword);
}
