package com.eventify.eventify_backend.unit.controller;

import com.eventify.eventify_backend.api.dto.request.LoginRequest;
import com.eventify.eventify_backend.api.dto.request.RegisterRequest;
import com.eventify.eventify_backend.api.dto.response.AuthResponse;
import com.eventify.eventify_backend.controller.AuthController;
import com.eventify.eventify_backend.service.AuthService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    @Test
    void register_ShouldReturnAuthResponse() {
        RegisterRequest request = RegisterRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        AuthResponse expectedResponse = AuthResponse.builder()
                .token("jwt-token")
                .role("USER")
                .build();

        when(authService.register(request)).thenReturn(expectedResponse);

        AuthResponse response = authController.register(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
    }

    @Test
    void login_ShouldReturnAuthResponse() {
        LoginRequest request = LoginRequest.builder()
                .email("test@example.com")
                .password("password123")
                .build();

        AuthResponse expectedResponse = AuthResponse.builder()
                .token("jwt-token")
                .role("USER")
                .build();

        when(authService.login(request)).thenReturn(expectedResponse);

        AuthResponse response = authController.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("jwt-token");
    }
}