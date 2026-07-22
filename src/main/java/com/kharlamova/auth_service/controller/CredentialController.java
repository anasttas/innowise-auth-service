package com.kharlamova.auth_service.controller;

import com.kharlamova.auth_service.dto.*;
import com.kharlamova.auth_service.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class CredentialController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterDto registerDto) {
        System.out.println("Controller");
        return new ResponseEntity<>(authService.register(registerDto), HttpStatus.OK);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginDto loginDto) {
        return new ResponseEntity<>(authService.login(loginDto), HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@RequestBody @Valid RefreshTokenDto refreshTokenDto) {
        return ResponseEntity.ok(authService.refresh(refreshTokenDto));
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validate(@RequestBody @Valid TokenDto tokenDto) {
        return ResponseEntity.ok(authService.validate(tokenDto));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/role")
    public ResponseEntity<RegisterResponse> changeRole(@RequestBody @Valid UpdateRoleRequest updateRoleRequest) {
        return new ResponseEntity<>(authService.changeRole(updateRoleRequest), HttpStatus.OK);
    }
}
