package com.finance.demo.model;

public enum RecordType {
    INCOME,
    EXPENSE;

    public static RecordType from(String value, RecordType fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        String normalized = value.trim().toUpperCase();
        try {
            return RecordType.valueOf(normalized);
        } catch (IllegalArgumentException ex) {
            return fallback;
        }
    }
}
