package com.eventify.eventify_backend.service;

import com.eventify.eventify_backend.api.dto.request.NotificationPreferencesRequest;
import com.eventify.eventify_backend.api.dto.response.NotificationPreferencesResponse;
import com.eventify.eventify_backend.domain.model.User;
import com.eventify.eventify_backend.domain.repository.UserRepository;
import com.eventify.eventify_backend.exception.DuplicateResourceException;
import com.eventify.eventify_backend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public User registerUser(String email, String rawPassword) {
        log.debug("Registering user with email: {}", email);

        if (userRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("User", email);
        }

        User user = User.builder()
                .email(email)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(User.Role.USER)
                .build();

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with email: {}, id: {}", email, savedUser.getId());
        return savedUser;
    }

    @Transactional(readOnly = true)
    public User findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", email));
    }


    @Transactional(readOnly = true)
    public User findById(Long id) {
        log.debug("Finding user by id: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", id));
    }

    @Transactional
    public void updateTelegramChatId(Long userId, String chatId) {
        log.debug("Updating telegram chat id for user: {}", userId);

        User user = findById(userId);
        user.setTelegramChatId(chatId);
        userRepository.save(user);

        log.info("Telegram chat id updated for user: {}", userId);
    }


    public NotificationPreferencesResponse getNotificationPreferences(Long userId) {
        return NotificationPreferencesResponse.builder()
                .notifyNewEvents(true)
                .notifyUpcoming(true)
                .notifyBeforeHours(24)
                .build();
    }

    public NotificationPreferencesResponse updateNotificationPreferences(Long userId, NotificationPreferencesRequest request) {
        return NotificationPreferencesResponse.builder()
                .notifyNewEvents(request.getNotifyNewEvents() != null ? request.getNotifyNewEvents() : true)
                .notifyUpcoming(request.getNotifyUpcoming() != null ? request.getNotifyUpcoming() : true)
                .notifyBeforeHours(request.getNotifyBeforeHours() != null ? request.getNotifyBeforeHours() : 24)
                .build();
    }

    public void deleteNotificationPreferences(Long userId) {

    }
}