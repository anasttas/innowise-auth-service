package com.kharlamova.auth_service.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);

        if (!jwtProvider.validateAccessToken(token)) {
            filterChain.doFilter(request, response);
            return;
        }

        Claims claims = jwtProvider.getAccessClaims(token);

        String login = claims.getSubject();

        String role = claims.get("role", String.class);

        Long userId = claims.get("userId", Long.class);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken (
                login,
                userId,
                List.of(new SimpleGrantedAuthority("ROLE_" + role))
        );

        authentication.setAuthenticated(true);

        SecurityContextHolder.getContext()
                .setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}
