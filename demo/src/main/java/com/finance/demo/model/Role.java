package com.finance.demo.model;

public enum Role {
    VIEWER,
    ANALYST,
    ADMIN;

    public static Role from(String value, Role fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        String normalized = value.trim().toUpperCase();
        if ("ANALYZER".equals(normalized)) {
            normalized = "ANALYST";
        }
        try {
            return Role.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            return fallback;
        }
    }
}
