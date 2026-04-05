package com.finance.demo.dto;

/**
 * Simple DTO returned by /auth/login.
 */
public record AuthResponse(String message, String token, String role) {

    public boolean isSuccess() {
        return token != null && !token.isBlank();
    }

    public static AuthResponse success(String message, String token, String role) {
        return new AuthResponse(message, token, role);
    }

    public static AuthResponse failure(String message) {
        return new AuthResponse(message, null, null);
    }
}
