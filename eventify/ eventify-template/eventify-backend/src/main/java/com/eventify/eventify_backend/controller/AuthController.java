package com.eventify.eventify_backend.controller;

import com.eventify.eventify_backend.api.dto.request.LoginRequest;
import com.eventify.eventify_backend.api.dto.request.RegisterRequest;
import com.eventify.eventify_backend.api.dto.response.AuthResponse;
import com.eventify.eventify_backend.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;


@Slf4j
@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Аутентификация и регистрация")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/register")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Регистрация пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Ошибка валидации")
    })
    public AuthResponse register(@Valid @RequestBody RegisterRequest request) {
        log.info("Register request for email: {}", request.getEmail());
        return authService.register(request);
    }

    @PostMapping("/auth/login")
    @Operation(summary = "Вход в систему")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешный вход"),
            @ApiResponse(responseCode = "401", description = "Неверное имя пользователя или пароль")
    })
    public AuthResponse login(@Valid @RequestBody LoginRequest request) {
        log.info("Login request for email: {}", request.getEmail());
        return authService.login(request);
    }
}