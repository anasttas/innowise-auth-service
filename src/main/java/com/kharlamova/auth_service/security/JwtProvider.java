package com.kharlamova.auth_service.security;

import com.kharlamova.auth_service.entity.Credential;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtProvider {
    @Value("${jwt.secret.access}")
    private String accessSecret;

    @Value("${jwt.secret.refresh}")
    private String refreshSecret;

    @Value("${security.jwt.access-expiration}")
    private long accessExpiration;

    @Value("${security.jwt.refresh-expiration}")
    private long refreshExpiration;

    private SecretKey generateAccessKey() {
        byte[] secretAsBytes = Decoders.BASE64.decode(accessSecret);

        return Keys.hmacShaKeyFor(secretAsBytes);
    }

    private SecretKey generateRefreshKey() {
        byte[] secretAsBytes = Decoders.BASE64.decode(refreshSecret);

        return Keys.hmacShaKeyFor(secretAsBytes);
    }

    public String generateAccessToken(Credential credential) {
        return Jwts.builder()
                .subject(credential.getLogin())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + accessExpiration))
                .claim("role", credential.getRole())
                .claim("userId", credential.getUserId())
                .signWith(generateAccessKey())
                .compact();
    }

    public String generateRefreshToken(Credential credential) {
        return Jwts.builder()
                .subject(credential.getLogin())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .claim("userId", credential.getUserId())
                .signWith(generateRefreshKey())
                .compact();
    }

    public boolean validateAccessToken(String accessToken) {
        return validateToken(accessToken, generateAccessKey());
    }

    public boolean validateRefreshToken(String refreshToken) {
        return validateToken(refreshToken, generateRefreshKey());
    }

    public Claims getAccessClaims(String token) {
        return getClaims(token, generateAccessKey());
    }

    public Claims getRefreshClaims(String token) {
        return getClaims(token, generateRefreshKey());
    }

    public Long extractUserId(String token) {
        Claims claims = getAccessClaims(token);

        return claims.get("userId", Long.class);
    }

    public String extractRole(String token) {
        Claims claims = getAccessClaims(token);

        return claims.get("role", String.class);
    }

    public String extractLogin(String token) {
        return getAccessClaims(token).getSubject();
    }

    private boolean validateToken(String token, SecretKey secret) {
        try {
            Jwts.parser()
                    .verifyWith(secret)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims getClaims(String token, SecretKey secret) {
        return Jwts.parser()
                .verifyWith(secret)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
