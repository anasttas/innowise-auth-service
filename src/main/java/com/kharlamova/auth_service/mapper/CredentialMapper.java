package com.kharlamova.auth_service.mapper;

import com.kharlamova.auth_service.dto.RegisterDto;
import com.kharlamova.auth_service.entity.Credential;
import com.kharlamova.auth_service.entity.Role;

public class CredentialMapper {
    public static Credential makeCredential(RegisterDto registerDto) {
        return Credential.builder()
                .login(registerDto.getLogin())
                .role(Role.USER)
                .build();
    }
}
