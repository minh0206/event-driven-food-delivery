package com.fooddelivery.userservice.controller;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fooddelivery.securitylib.config.JwtConfig;
import com.fooddelivery.securitylib.service.JwtService;
import com.fooddelivery.userservice.dto.LoginRequestDto;
import com.fooddelivery.userservice.dto.RegisterRequestDto;
import com.fooddelivery.userservice.dto.UserDto;
import com.fooddelivery.userservice.mapper.UserMapper;
import com.fooddelivery.userservice.model.Role;
import com.fooddelivery.userservice.model.Token;
import com.fooddelivery.userservice.model.User;
import com.fooddelivery.userservice.service.TokenService;
import com.fooddelivery.userservice.service.UserService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@Slf4j
@AllArgsConstructor
public class UserController {
    private static final String ACCESS_TOKEN_KEY = "accessToken";
    private static final String REFRESH_TOKEN_KEY = "refreshToken";
    private final UserService userService;
    private final JwtConfig jwtConfig;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final TokenService tokenService;

    private Cookie createCookie(String key, String value, String path, int maxAge) {
        Cookie cookie = new Cookie(key, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // Set to true in production with HTTPS
        cookie.setPath(path);
        cookie.setMaxAge(maxAge);
        return cookie;
    }

    @PostMapping("/register/customer")
    public Map<String, String> registerCustomer(@Valid @RequestBody RegisterRequestDto requestDto) {
        User registeredUser = userService.registerCustomer(requestDto);
        String accessToken = jwtService.generateAccessToken(
                registeredUser.getId().toString(),
                registeredUser.getRole().toString());

        // Save token to database
        Claims claims = jwtService.extractAllClaims(accessToken);
        tokenService.saveRefreshToken(accessToken, registeredUser.getId(), claims.getExpiration());

        return Map.of(ACCESS_TOKEN_KEY, accessToken);
    }

    @PostMapping("/register/restaurant")
    public Map<String, String> registerRestaurantAdmin(
            @Valid @RequestBody RegisterRequestDto requestDto) {
        User registeredUser = userService.registerRestaurantAdmin(requestDto);
        String accessToken = jwtService.generateAccessToken(
                registeredUser.getId().toString(),
                registeredUser.getRole().toString());

        // Save token to database
        Claims claims = jwtService.extractAllClaims(accessToken);
        tokenService.saveRefreshToken(accessToken, registeredUser.getId(), claims.getExpiration());

        return Map.of(ACCESS_TOKEN_KEY, accessToken);
    }

    @PostMapping("/register/driver")
    public Map<String, String> registerDriver(@Valid @RequestBody RegisterRequestDto requestDto) {
        User registeredUser = userService.registerDriver(requestDto);
        String accessToken = jwtService.generateAccessToken(
                registeredUser.getId().toString(),
                registeredUser.getRole().toString());

        // Save token to database
        Claims claims = jwtService.extractAllClaims(accessToken);
        tokenService.saveRefreshToken(accessToken, registeredUser.getId(), claims.getExpiration());

        return Map.of(ACCESS_TOKEN_KEY, accessToken);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(
            @RequestBody LoginRequestDto requestDto,
            HttpServletResponse response) {
        User user = userService.loginUser(requestDto.email(), requestDto.password());

        // Check if user has a valid refresh token
        String refreshToken;
        Optional<Token> existingToken = tokenService
                .getValidTokenByUserId(user.getId());

        if (existingToken.isPresent()) {
            // Use existing valid refresh token
            refreshToken = existingToken.get().getRefreshToken();
            log.info("Reusing existing valid refresh token for user: {}", user.getId());
        } else {
            // Generate new refresh token only if no valid token exists
            refreshToken = jwtService.generateRefreshToken(
                    user.getId().toString(),
                    user.getRole().toString());

            // Save new refresh token to database
            Claims refreshClaims = jwtService.extractAllClaims(refreshToken);
            tokenService.saveRefreshToken(refreshToken, user.getId(), refreshClaims.getExpiration());
            log.info("Created new refresh token for user: {}", user.getId());
        }

        // Always generate a new access token
        String accessToken = jwtService.generateAccessToken(
                user.getId().toString(),
                user.getRole().toString());

        response.addCookie(
                createCookie(REFRESH_TOKEN_KEY, refreshToken, "/api/users/refresh", jwtConfig.getRefreshExpiration()));
        return ResponseEntity.ok(Map.of(ACCESS_TOKEN_KEY, accessToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshAccessToken(@CookieValue(REFRESH_TOKEN_KEY) String refreshToken) {
        try {
            // This will throw an exception if the token is expired or invalid
            jwtService.validateToken(refreshToken);
        } catch (JwtException e) {
            log.error(e.getMessage());
            throw e;
        }

        // Check if token is revoked
        if (!tokenService.isTokenValid(refreshToken)) {
            log.error("Refresh token has been revoked");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Claims claims = jwtService.extractAllClaims(refreshToken);
        String userId = claims.getSubject();
        // Check if user exists
        User user = userService.getUserById(Long.parseLong(userId));
        if (user == null) {
            log.error("Refresh token belongs to non-existent user: {}", userId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // Check role
        String role = claims.get("role", String.class);
        if (!role.equals("ROLE_" + user.getRole().toString())) {
            log.error("Role in refresh token does not match user role: {} != {}", role, user.getRole().toString());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String accessToken = jwtService.generateAccessToken(userId, role);
        return ResponseEntity.ok(Map.of(ACCESS_TOKEN_KEY, accessToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            Principal principal,
            HttpServletResponse response) {
        // Revoke all tokens for this user
        Long userId = Long.parseLong(principal.getName());
        tokenService.revokeAllUserTokens(userId);
        log.info("User {} logged out successfully", userId);

        // Clear refresh token cookie
        response.addCookie(createCookie(REFRESH_TOKEN_KEY, "", "/api/users/refresh", 0));

        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/profile")
    public UserDto getUserProfile(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        User user = userService.getUserById(userId);

        switch (user.getRole()) {
            case Role.RESTAURANT_ADMIN:
                return userMapper.toRestaurantAdminDto(user);
            case Role.DELIVERY_DRIVER:
                return userMapper.toDriverDto(user);
            default:
                return userMapper.toCustomerDto(user);
        }
    }
}