
package com.eventify.eventify_backend.service;

import com.eventify.eventify_backend.api.dto.request.LoginRequest;
import com.eventify.eventify_backend.api.dto.request.RegisterRequest;
import com.eventify.eventify_backend.api.dto.response.AuthResponse;
import com.eventify.eventify_backend.domain.model.User;
import com.eventify.eventify_backend.exception.BusinessException;
import com.eventify.eventify_backend.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        log.debug("Registering new user with email: {}", request.getEmail());

        User user = userService.registerUser(request.getEmail(), request.getPassword());

        String token = tokenProvider.generateToken(user.getEmail(), user.getRole().name());

        log.info("User registered successfully: {}", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        log.debug("Login attempt for user: {}", request.getEmail());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        User user = userService.findByEmail(request.getEmail());
        String token = tokenProvider.generateToken(user.getEmail(), user.getRole().name());

        log.info("User logged in successfully: {}", user.getEmail());

        return AuthResponse.builder()
                .token(token)
                .role(user.getRole().name())
                .build();
    }
}