package com.kharlamova.auth_service.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class AuthResponse {
    private String accessToken;

    private String refreshToken;
}
