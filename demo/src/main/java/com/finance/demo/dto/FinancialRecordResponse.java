package com.finance.demo.dto;

import com.finance.demo.model.RecordType;
import java.math.BigDecimal;
import java.time.LocalDate;

public record FinancialRecordResponse(
        Integer id,
        Integer createdBy,
        RecordType type,
        String category,
        BigDecimal amount,
        LocalDate date,
        String notes
) {
}
