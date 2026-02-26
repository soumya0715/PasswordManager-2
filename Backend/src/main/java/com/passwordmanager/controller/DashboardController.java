package com.passwordmanager.controller;

import com.passwordmanager.dto.response.ApiResponse;
import com.passwordmanager.dto.response.DashboardResponse;
import com.passwordmanager.entity.Password;
import com.passwordmanager.entity.User;
import com.passwordmanager.repository.PasswordRepository;
import com.passwordmanager.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final PasswordRepository passwordRepository;
    private final UserRepository userRepository;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboard(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));
        List<Password> all = passwordRepository.findByUserOrderByCreatedAtDesc(user);

        DashboardResponse response = new DashboardResponse();
        response.setTotalPasswords(all.size());
        response.setWeakPasswords(all.stream().filter(p -> p.getStrengthScore() != null && p.getStrengthScore() < 40).count());
        response.setFavoritePasswords(all.stream().filter(p -> Boolean.TRUE.equals(p.getIsFavorite())).count());

        // Check reused
        Map<String, Long> passwordCounts = all.stream().collect(Collectors.groupingBy(Password::getPassword, Collectors.counting()));
        response.setReusedPasswords(passwordCounts.values().stream().filter(c -> c > 1).count());

        List<DashboardResponse.RecentPassword> recent = all.stream().limit(5).map(p -> {
            DashboardResponse.RecentPassword rp = new DashboardResponse.RecentPassword();
            rp.setId(p.getId());
            rp.setTitle(p.getTitle());
            rp.setCategoryName(p.getCategory() != null ? p.getCategory().getName() : "Other");
            rp.setCreatedAt(p.getCreatedAt() != null ? p.getCreatedAt().toString() : "");
            return rp;
        }).collect(Collectors.toList());
        response.setRecentPasswords(recent);

        return ResponseEntity.ok(ApiResponse.success(response, "Dashboard data"));
    }
}
