package com.fooddelivery.userservice.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fooddelivery.userservice.model.RefreshToken;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    @EntityGraph(attributePaths = "user")
    List<RefreshToken> findAllByOrderByCreatedAtDesc();

    Optional<RefreshToken> findByToken(String token);

    @Modifying
    @Query("UPDATE RefreshToken t SET t.revoked = true, t.revokedAt = :revokedAt WHERE t.user.id = :userId AND t.revoked = false")
    void revokeAllUserTokens(Long userId, LocalDateTime revokedAt);

    @Modifying
    @Query("DELETE FROM RefreshToken t WHERE t.expiresAt < :now OR t.revoked = true")
    void deleteExpiredAndRevokedTokens(LocalDateTime now);

    @Query("SELECT t FROM RefreshToken t WHERE t.user.id = :userId AND t.revoked = false AND t.expiresAt > :now ORDER BY t.expiresAt DESC")
    Optional<RefreshToken> findValidTokenByUserId(Long userId, LocalDateTime now);
}
