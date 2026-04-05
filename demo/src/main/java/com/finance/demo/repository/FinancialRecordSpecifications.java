package com.finance.demo.repository;

import com.finance.demo.model.FinancialRecord;
import com.finance.demo.model.RecordType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public final class FinancialRecordSpecifications {

    private FinancialRecordSpecifications() {
    }

    public static Specification<FinancialRecord> withFilters(
            RecordType type,
            String category,
            LocalDate startDate,
            LocalDate endDate,
            Integer createdBy
    ) {
        return (root, query, builder) -> {
            var predicates = builder.conjunction();

            if (type != null) {
                predicates.getExpressions().add(builder.equal(root.get("type"), type));
            }
            if (category != null && !category.isBlank()) {
                predicates.getExpressions().add(builder.equal(root.get("category"), category));
            }
            if (createdBy != null) {
                predicates.getExpressions().add(builder.equal(root.get("createdBy"), createdBy));
            }
            if (startDate != null && endDate != null) {
                predicates.getExpressions().add(builder.between(root.get("date"), startDate, endDate));
            } else if (startDate != null) {
                predicates.getExpressions().add(builder.greaterThanOrEqualTo(root.get("date"), startDate));
            } else if (endDate != null) {
                predicates.getExpressions().add(builder.lessThanOrEqualTo(root.get("date"), endDate));
            }

            return predicates;
        };
    }
}
