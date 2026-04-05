package com.finance.demo.model;

public enum UserStatus {
    ACTIVE,
    INACTIVE;

    public static UserStatus from(String value, UserStatus fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        String normalized = value.trim().toUpperCase();
        try {
            return UserStatus.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            return fallback;
        }
    }
}
