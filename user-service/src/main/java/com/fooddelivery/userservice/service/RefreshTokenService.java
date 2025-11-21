package com.fooddelivery.userservice.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fooddelivery.securitylib.service.TokenValidationService;
import com.fooddelivery.userservice.model.RefreshToken;
import com.fooddelivery.userservice.model.User;
import com.fooddelivery.userservice.repository.RefreshTokenRepository;
import com.fooddelivery.userservice.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class RefreshTokenService implements TokenValidationService {
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;

    @Transactional
    public RefreshToken saveRefreshToken(String tokenValue, Long userId, Date expirationDate) {
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        RefreshToken token = new RefreshToken();
        token.setToken(tokenValue);
        token.setExpiresAt(convertToLocalDateTime(expirationDate));
        token.setUser(user);

        return refreshTokenRepository.save(token);
    }

    @Override
    public boolean isTokenValid(String tokenValue) {
        Optional<RefreshToken> tokenOpt = refreshTokenRepository.findByToken(tokenValue);
        if (tokenOpt.isEmpty()) {
            // Token not found in database - might be a new token or not yet saved
            return true; // Allow it for backward compatibility
        }

        RefreshToken token = tokenOpt.get();
        return !token.isRevoked() && token.getExpiresAt().isAfter(LocalDateTime.now());
    }

    @Transactional
    public void revokeRefreshToken(String tokenValue) {
        RefreshToken token = refreshTokenRepository.findByToken(tokenValue)
                .orElseThrow(() -> new EntityNotFoundException("Token not found"));

        if (token != null) {
            token.setRevoked(true);
            token.setRevokedAt(LocalDateTime.now());
            refreshTokenRepository.save(token);
            log.info("Token revoked: {}", token.getToken());
        }
    }

    @Transactional
    public void revokeAllUserTokens(Long userId) {
        refreshTokenRepository.revokeAllUserTokens(userId, LocalDateTime.now());
        log.info("All tokens revoked for user: {}", userId);
    }

    public Optional<RefreshToken> getValidTokenByUserId(Long userId) {
        return refreshTokenRepository.findValidTokenByUserId(userId, LocalDateTime.now());
    }

    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    @Transactional
    public void cleanupExpiredAndRevokedTokens() {
        refreshTokenRepository.deleteExpiredAndRevokedTokens(LocalDateTime.now());
        log.info("Expired and revoked tokens cleaned up");
    }

    public List<RefreshToken> getAllTokens() {
        return refreshTokenRepository.findAllByOrderByCreatedAtDesc();
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
