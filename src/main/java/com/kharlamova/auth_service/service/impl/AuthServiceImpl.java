package com.kharlamova.auth_service.service.impl;

import com.kharlamova.auth_service.dto.*;
import com.kharlamova.auth_service.mapper.CredentialMapper;
import com.kharlamova.auth_service.repository.CredentialRepository;
import com.kharlamova.auth_service.security.JwtProvider;
import com.kharlamova.auth_service.service.AuthService;
import com.kharlamova.auth_service.entity.Credential;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final CredentialRepository credentialRepository;

    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;

    private final Map<String, String> refreshStorage = new HashMap<>();

    @Override
    public AuthResponse login(LoginDto loginDto) {
        Credential credential = credentialRepository.findByLogin(loginDto.getLogin())
                .orElseThrow(() -> new RuntimeException("Login not found"));

        if(!passwordEncoder.matches(loginDto.getPassword(), credential.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        final String accessToken = jwtProvider.generateAccessToken(credential);
        final String refreshToken = jwtProvider.generateRefreshToken(credential);

        refreshStorage.put(credential.getLogin(), refreshToken);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public RegisterResponse register(RegisterDto registerDto) {
        credentialRepository
                .findByLogin(registerDto.getLogin())
                .ifPresent(foundLogin -> {
                    throw new RuntimeException("Login already exists " + foundLogin.getLogin());
                });

        Credential credential = CredentialMapper.makeCredential(registerDto);

        credential.setPassword(passwordEncoder.encode(registerDto.getPassword()));

        credentialRepository.save(credential);

        return new RegisterResponse("User registered successfully");
    }

    @Override
    public AuthResponse refresh(RefreshTokenDto refreshTokenDto) {
        String refreshToken = refreshTokenDto.getRefreshToken();

        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            throw new RuntimeException("Invalid refresh token");
        }

        Claims claims = jwtProvider.getRefreshClaims(refreshToken);

        String login = claims.getSubject();

        Credential credential = credentialRepository.findByLogin(login)
                .orElseThrow(() -> new RuntimeException("User not found"));

        String newAccessToken = jwtProvider.generateAccessToken(credential);

        String newRefreshToken = jwtProvider.generateRefreshToken(credential);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    @Override
    public boolean validate(TokenDto tokenDto) {
        return jwtProvider.validateAccessToken(tokenDto.getToken());
    }
}
