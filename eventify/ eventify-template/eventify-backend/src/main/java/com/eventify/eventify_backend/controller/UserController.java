package com.eventify.eventify_backend.controller;

import com.eventify.eventify_backend.api.dto.request.NotificationPreferencesRequest;
import com.eventify.eventify_backend.api.dto.response.NotificationPreferencesResponse;
import com.eventify.eventify_backend.security.SecurityUtils;
import com.eventify.eventify_backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User", description = "Настройки уведомлений пользователя")
public class UserController {

    private final UserService userService;

    @GetMapping("/notifications")
    @Operation(summary = "Получить настройки уведомлений")
    public NotificationPreferencesResponse getNotificationPreferences() {
        String email = SecurityUtils.getCurrentUserEmail();
        Long userId = userService.findByEmail(email).getId();
        log.info("GET /user/notifications - user: {}", userId);
        return userService.getNotificationPreferences(userId);
    }

    @PutMapping("/notifications")
    @Operation(summary = "Обновить настройки уведомлений")
    public NotificationPreferencesResponse updateNotificationPreferences(@Valid @RequestBody NotificationPreferencesRequest request) {
        String email = SecurityUtils.getCurrentUserEmail();
        Long userId = userService.findByEmail(email).getId();
        log.info("PUT /user/notifications - user: {}", userId);
        return userService.updateNotificationPreferences(userId, request);
    }

    @DeleteMapping("/notifications")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Отменить уведомления")
    public void deleteNotificationPreferences() {
        String email = SecurityUtils.getCurrentUserEmail();
        Long userId = userService.findByEmail(email).getId();
        log.info("DELETE /user/notifications - user: {}", userId);
        userService.deleteNotificationPreferences(userId);
    }

    @PostMapping("/telegram/link")
    @Operation(summary = "Инициировать привязку Telegram")
    public String linkTelegram() {
        String email = SecurityUtils.getCurrentUserEmail();
        Long userId = userService.findByEmail(email).getId();
        log.info("POST /user/telegram/link - user: {}", userId);
        return "TG_LINK_CODE_" + userId + "_" + System.currentTimeMillis();
    }
}