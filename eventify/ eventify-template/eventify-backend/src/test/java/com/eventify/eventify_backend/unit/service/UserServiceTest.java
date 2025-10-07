package com.eventify.eventify_backend.unit.service;

import com.eventify.eventify_backend.domain.model.User;
import com.eventify.eventify_backend.domain.repository.UserRepository;
import com.eventify.eventify_backend.exception.DuplicateResourceException;
import com.eventify.eventify_backend.exception.ResourceNotFoundException;
import com.eventify.eventify_backend.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private final String testEmail = "test@example.com";
    private final String testPassword = "password123";

    @Test
    void registerUser_Success_ShouldCreateUser() {
        when(userRepository.existsByEmail(testEmail)).thenReturn(false);
        when(passwordEncoder.encode(testPassword)).thenReturn("encoded_password");

        User savedUser = User.builder()
                .id(1L)
                .email(testEmail)
                .passwordHash("encoded_password")
                .role(User.Role.USER)
                .build();

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = userService.registerUser(testEmail, testPassword);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(testEmail);
        verify(userRepository).save(any(User.class));
    }

    @Test
    void registerUser_DuplicateEmail_ShouldThrowException() {
        when(userRepository.existsByEmail(testEmail)).thenReturn(true);

        assertThatThrownBy(() -> userService.registerUser(testEmail, testPassword))
                .isInstanceOf(DuplicateResourceException.class);

        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void findByEmail_UserExists_ShouldReturnUser() {
        User user = User.builder().id(1L).email(testEmail).build();
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(user));

        User result = userService.findByEmail(testEmail);

        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(testEmail);
    }

    @Test
    void findByEmail_UserNotFound_ShouldThrowException() {
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findByEmail(testEmail))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void findById_UserExists_ShouldReturnUser() {
        User user = User.builder().id(1L).email(testEmail).build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userService.findById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
    }

    @Test
    void findById_UserNotFound_ShouldThrowException() {
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}