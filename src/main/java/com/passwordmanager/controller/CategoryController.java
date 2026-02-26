package com.passwordmanager.controller;

import com.passwordmanager.entity.Category;
import com.passwordmanager.entity.User;
import com.passwordmanager.repository.CategoryRepository;
import com.passwordmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    private User getAuthenticatedUser(Authentication auth) {
        return userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @GetMapping("/GetCategory")
    public ResponseEntity<List<Category>> getAll(Authentication auth) {
        User user = getAuthenticatedUser(auth);
        return ResponseEntity.ok(categoryRepository.findByUser(user));
    }

    @PostMapping("/CreateCategory")
    public ResponseEntity<Category> createCategory(
            @RequestBody Map<String, String> body,
            Authentication auth) {

        User user = getAuthenticatedUser(auth);

        Category category = new Category();
        category.setName(body.get("name"));
        category.setDescription(body.get("description"));
        category.setUser(user);

        return ResponseEntity.ok(categoryRepository.save(category));
    }
}