package com.fooddelivery.securitylib.service;

public interface TokenValidationService {
    boolean isTokenValid(String token);
}
