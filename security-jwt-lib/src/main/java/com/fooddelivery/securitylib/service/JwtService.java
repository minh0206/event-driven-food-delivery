package com.fooddelivery.securitylib.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public class JwtService {
    @Value("${spring.jwt.expiration}")
    private long tokenExpiration;

    @Value("${spring.jwt.secret}")
    private String secret;

    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String id, String role) {
        return Jwts.builder()
                .setSubject(id)
                .claim("role", "ROLE_" + role)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration * 1000))
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token); // This will throw an exception if the token is invalid
            return !claims.getExpiration().before(new Date());
        } catch (Exception e) {
            // Log the exception (e.g., MalformedJwtException, ExpiredJwtException)
            return false;
        }
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build().
                parseClaimsJws(token)
                .getBody();
    }
}