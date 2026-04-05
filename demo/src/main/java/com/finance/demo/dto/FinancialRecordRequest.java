package com.finance.demo.dto;

import com.finance.demo.model.RecordType;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public record FinancialRecordRequest(
        @NotNull(message = "Type is required")
        RecordType type,
        @NotBlank(message = "Category is required")
        String category,
        @NotNull(message = "Amount is required")
        @DecimalMin(value = "0.01", message = "Amount must be positive")
        BigDecimal amount,
        @NotNull(message = "Date is required")
        @PastOrPresent(message = "Date cannot be in the future")
        LocalDate date,
        @Size(max = 500, message = "Notes must be at most 500 characters")
        String notes,
        Integer createdBy
) {
}
