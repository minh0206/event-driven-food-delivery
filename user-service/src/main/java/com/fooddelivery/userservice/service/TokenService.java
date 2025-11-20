package com.fooddelivery.userservice.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Optional;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fooddelivery.securitylib.service.TokenValidationService;
import com.fooddelivery.userservice.model.Token;
import com.fooddelivery.userservice.repository.TokenRepository;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class TokenService implements TokenValidationService {
    private final TokenRepository tokenRepository;

    @Transactional
    public void saveRefreshToken(String tokenValue, Long userId, Date expirationDate) {
        Token token = new Token();
        token.setRefreshToken(tokenValue);
        token.setUserId(userId);
        token.setExpiresAt(convertToLocalDateTime(expirationDate));
        token.setRevoked(false);
        tokenRepository.save(token);
    }

    @Override
    public boolean isTokenValid(String tokenValue) {
        Optional<Token> tokenOpt = tokenRepository.findByRefreshToken(tokenValue);
        if (tokenOpt.isEmpty()) {
            // Token not found in database - might be a new token or not yet saved
            return true; // Allow it for backward compatibility
        }

        Token token = tokenOpt.get();
        return !token.isRevoked() && token.getExpiresAt().isAfter(LocalDateTime.now());
    }

    @Transactional
    public void revokeRefreshToken(String tokenValue) {
        Optional<Token> tokenOpt = tokenRepository.findByRefreshToken(tokenValue);
        if (tokenOpt.isPresent()) {
            Token token = tokenOpt.get();
            token.setRevoked(true);
            token.setRevokedAt(LocalDateTime.now());
            tokenRepository.save(token);
            log.info("Token revoked for user: {}", token.getUserId());
        }
    }

    @Transactional
    public void revokeAllUserTokens(Long userId) {
        tokenRepository.revokeAllUserTokens(userId, LocalDateTime.now());
        log.info("All tokens revoked for user: {}", userId);
    }

    @Scheduled(cron = "0 0 2 * * ?") // Run daily at 2 AM
    @Transactional
    public void cleanupExpiredTokens() {
        tokenRepository.deleteExpiredTokens(LocalDateTime.now());
        log.info("Expired tokens cleaned up");
    }

    public java.util.List<Token> getAllTokens() {
        return tokenRepository.findAll();
    }

    public Optional<Token> getValidTokenByUserId(Long userId) {
        return tokenRepository.findValidTokenByUserId(userId, LocalDateTime.now());
    }

    private LocalDateTime convertToLocalDateTime(Date date) {
        return Instant.ofEpochMilli(date.getTime())
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
