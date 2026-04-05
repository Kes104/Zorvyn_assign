package com.finance.demo.dto;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        String message,
        Map<String, String> details,
        Instant timestamp,
        String path
) {
}
