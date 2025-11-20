package com.fooddelivery.userservice.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fooddelivery.userservice.model.Token;

@Repository
public interface TokenRepository extends JpaRepository<Token, Long> {
    Optional<Token> findByRefreshToken(String refreshToken);

    @Modifying
    @Query("UPDATE Token t SET t.revoked = true, t.revokedAt = :revokedAt WHERE t.userId = :userId AND t.revoked = false")
    void revokeAllUserTokens(Long userId, LocalDateTime revokedAt);

    @Modifying
    @Query("DELETE FROM Token t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(LocalDateTime now);

    @Query("SELECT t FROM Token t WHERE t.userId = :userId AND t.revoked = false AND t.expiresAt > :now ORDER BY t.expiresAt DESC")
    Optional<Token> findValidTokenByUserId(Long userId, LocalDateTime now);
}
