package com.passwordmanager.dto.response;

import lombok.Data;
import java.util.List;

@Data
public class SecurityAuditResponse {
    private List<AuditItem> weakPasswords;
    private List<AuditItem> reusedPasswords;
    private List<AuditItem> oldPasswords;
    private int overallScore;

    @Data
    public static class AuditItem {
        private Long id;
        private String title;
        private String issue;
        private int strengthScore;
    }
}
