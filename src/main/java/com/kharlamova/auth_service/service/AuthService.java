package com.kharlamova.auth_service.service;

import com.kharlamova.auth_service.dto.*;

public interface AuthService {
    RegisterResponse register(RegisterDto registerDto);

    AuthResponse login(LoginDto loginDto);

    AuthResponse refresh(RefreshTokenDto refreshTokenDto);

    boolean validate(TokenDto tokenDto);
}
