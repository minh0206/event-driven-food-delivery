package com.fooddelivery.userservice.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.userservice.dto.TokenDto;
import com.fooddelivery.userservice.mapper.TokenMapper;
import com.fooddelivery.userservice.service.TokenService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users/tokens")
@Slf4j
@AllArgsConstructor
public class TokenController {
    private final TokenService tokenService;
    private final TokenMapper tokenMapper;

    @GetMapping
    @PreAuthorize("hasRole('SYSTEM_ADMIN')")
    public ResponseEntity<List<TokenDto>> getAllTokens() {
        List<TokenDto> tokenDtos = tokenService.getAllTokens().stream()
                .map(tokenMapper::toDto)
                .collect(Collectors.toList());

        log.info("Retrieved {} tokens", tokenDtos.size());
        return ResponseEntity.ok(tokenDtos);
    }
}
