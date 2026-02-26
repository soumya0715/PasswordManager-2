package com.passwordmanager.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class DashboardResponse {
    private long totalPasswords;
    private long weakPasswords;
    private long reusedPasswords;
    private long favoritePasswords;
    private List<RecentPassword> recentPasswords;

    @Data
    public static class RecentPassword {
        private Long id;
        private String title;
        private String categoryName;
        private String createdAt;
    }
}
