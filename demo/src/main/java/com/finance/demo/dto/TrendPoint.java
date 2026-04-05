package com.finance.demo.dto;

import java.math.BigDecimal;

public record TrendPoint(
        String month,
        BigDecimal income,
        BigDecimal expense,
        BigDecimal net
) {
}
