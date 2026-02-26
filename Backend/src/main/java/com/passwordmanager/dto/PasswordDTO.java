package com.passwordmanager.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordDTO {
    private Long id;
    private String title;
    private String username;
    private String password;
    private String email;
    private String url;
    private String notes;
    private Integer strengthScore;
    private Boolean isFavorite;
    private Long categoryId;
    private String categoryName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
