package com.kharlamova.auth_service.service.impl;

import com.kharlamova.auth_service.client.UserRequest;
import com.kharlamova.auth_service.client.UserResponse;
import com.kharlamova.auth_service.client.UserServiceClient;
import com.kharlamova.auth_service.dto.*;
import com.kharlamova.auth_service.exception.*;
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

    private final UserServiceClient userServiceClient;

    @Override
    public AuthResponse login(LoginDto loginDto) {
        Credential credential = credentialRepository.findByLogin(loginDto.getLogin())
                .orElseThrow(() -> new InvalidCredentialsException("Wrong password or login"));

        if(!passwordEncoder.matches(loginDto.getPassword(), credential.getPassword())) {
            throw new InvalidCredentialsException("Wrong password or login");
        }

        String accessToken = jwtProvider.generateAccessToken(credential);
        String refreshToken = jwtProvider.generateRefreshToken(credential);

        refreshStorage.put(credential.getLogin(), refreshToken);

        return new AuthResponse(accessToken, refreshToken);
    }

    @Override
    public RegisterResponse register(RegisterDto registerDto) {
        credentialRepository
                .findByLogin(registerDto.getLogin())
                .ifPresent(foundLogin -> {
                    throw new LoginAlreadyExistsException("Login already exists " + foundLogin.getLogin());
                });

        Long userId = null;

        try {
            UserResponse userResponse = userServiceClient.createUser(
                    new UserRequest(
                            registerDto.getEmail(),
                            registerDto.getName(),
                            registerDto.getSurname(),
                            registerDto.getBirthDate()
                    )
            );

            userId = userResponse.getId();

            Credential credential = CredentialMapper.makeCredential(registerDto);

            credential.setUserId(userId);

            credential.setPassword(passwordEncoder.encode(registerDto.getPassword()));

            credentialRepository.save(credential);

            return new RegisterResponse("User registered successfully");
        }
        catch (Exception e) {
            if(userId != null){
                try {
                    userServiceClient.rollbackUserCreation(userId);
                } catch(Exception rollbackException){
                    throw new RegistrationException("Registration failed and rollback failed", rollbackException);
                }
            }
            throw new RegistrationException("Failed to register user", e);
        }
    }

    @Override
    public AuthResponse refresh(RefreshTokenDto refreshTokenDto) {
        String refreshToken = refreshTokenDto.getRefreshToken();

        if (!jwtProvider.validateRefreshToken(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }

        Claims claims = jwtProvider.getRefreshClaims(refreshToken);

        String login = claims.getSubject();

        String storedToken = refreshStorage.get(login);

        if(storedToken == null || !storedToken.equals(refreshToken)) {
            throw new InvalidTokenException("Refresh token expired or revoked");
        }

        Credential credential = credentialRepository.findByLogin(login)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        String newAccessToken = jwtProvider.generateAccessToken(credential);

        String newRefreshToken = jwtProvider.generateRefreshToken(credential);

        refreshStorage.put(login, newRefreshToken);

        return new AuthResponse(newAccessToken, newRefreshToken);
    }

    @Override
    public boolean validate(TokenDto tokenDto) {
        return jwtProvider.validateAccessToken(tokenDto.getToken());
    }

    @Override
    public RegisterResponse changeRole(UpdateRoleRequest updateRoleRequest) {
        Credential credential = credentialRepository.findByLogin(updateRoleRequest.getLogin())
                .orElseThrow(() -> new InvalidCredentialsException("Wrong password or login"));

        credential.setRole(updateRoleRequest.getRole());

        credentialRepository.save(credential);

        return new RegisterResponse("User role updated successfully");
    }
}
