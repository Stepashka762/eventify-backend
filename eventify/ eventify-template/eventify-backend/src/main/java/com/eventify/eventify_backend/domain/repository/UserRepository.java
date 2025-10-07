package com.eventify.eventify_backend.domain.repository;

import com.eventify.eventify_backend.domain.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    boolean existsByEmail(String email);

    Optional<User> findByTelegramChatId(String telegramChatId);

    Page<User> findByRole(User.Role role, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.telegramChatId IS NOT NULL AND u.telegramChatId != ''")
    Page<User> findUsersWithTelegram(Pageable pageable);

    @Modifying
    @Query("UPDATE User u SET u.telegramChatId = :chatId WHERE u.id = :userId")
    int updateTelegramChatId(@Param("userId") Long userId, @Param("chatId") String chatId);

    long countByRole(User.Role role);
}