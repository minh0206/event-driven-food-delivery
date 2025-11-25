package com.fooddelivery.userservice.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.userservice.dto.RefreshTokenDto;
import com.fooddelivery.userservice.mapper.RefreshTokenMapper;
import com.fooddelivery.userservice.service.RefreshTokenService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users/tokens")
@Slf4j
@AllArgsConstructor
public class RefreshTokenController {
    private final RefreshTokenService refreshTokenService;
    private final RefreshTokenMapper refreshTokenMapper;

    @GetMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public List<RefreshTokenDto> getAllTokens() {
        List<RefreshTokenDto> tokenDtos = refreshTokenService.getAllTokens().stream()
                .map(refreshTokenMapper::toDto)
                .toList();

        log.info("Retrieved {} tokens", tokenDtos.size());
        return tokenDtos;
    }

    @DeleteMapping("/prune")
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<String> pruneTokens() {
        refreshTokenService.cleanupExpiredAndRevokedTokens();
        log.info("Expired and revoked tokens pruned by admin");
        return ResponseEntity.ok("Expired and revoked tokens have been pruned successfully");
    }
}
