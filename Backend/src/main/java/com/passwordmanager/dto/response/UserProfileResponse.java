package com.passwordmanager.dto.response;

import lombok.Data;

@Data
public class UserProfileResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Boolean twoFAEnabled;
    private String createdAt;
}
