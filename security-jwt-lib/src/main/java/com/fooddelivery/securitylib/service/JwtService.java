package com.fooddelivery.securitylib.service;

import java.util.Date;

import com.fooddelivery.securitylib.config.JwtConfig;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@AllArgsConstructor
@Slf4j
public class JwtService {
    private final JwtConfig jwtConfig;

    public String generateAccessToken(String id, String role) {
        // Ensure role has ROLE_ prefix, but don't double-prefix
        String normalizedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return Jwts.builder()
                .setSubject(id)
                .claim("role", normalizedRole)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getAccessExpiration() * 1000))
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }

    public String generateRefreshToken(String id, String role) {
        // Ensure role has ROLE_ prefix, but don't double-prefix
        String normalizedRole = role.startsWith("ROLE_") ? role : "ROLE_" + role;
        return Jwts.builder()
                .setSubject(id)
                .claim("role", normalizedRole)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtConfig.getRefreshExpiration() * 1000))
                .signWith(jwtConfig.getSecretKey())
                .compact();
    }

    public void validateToken(String token) throws ExpiredJwtException {
        Jwts.parserBuilder()
                .setSigningKey(jwtConfig.getSecretKey())
                .build()
                .parseClaimsJws(token);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtConfig.getSecretKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}