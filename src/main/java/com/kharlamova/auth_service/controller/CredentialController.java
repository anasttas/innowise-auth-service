package com.kharlamova.auth_service.controller;

import com.kharlamova.auth_service.dto.*;
import com.kharlamova.auth_service.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class CredentialController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody  RegisterDto registerDto) {
        return new ResponseEntity<>(authService.register(registerDto), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginDto loginDto) {
        return new ResponseEntity<>(authService.login(loginDto), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody RefreshTokenDto refreshTokenDto) {
        return ResponseEntity.ok(authService.refresh(refreshTokenDto));
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestBody TokenDto tokenDto) {
        return ResponseEntity.ok(authService.validate(tokenDto));
    }
}
