package com.finance.demo.repository;

import com.finance.demo.model.FinancialRecord;
import com.finance.demo.model.RecordType;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;

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
            List<Predicate> predicates = new ArrayList<>();

            if (type != null) {
                predicates.add(builder.equal(root.get("type"), type));
            }
            if (category != null && !category.isBlank()) {
                predicates.add(builder.equal(root.get("category"), category));
            }
            if (createdBy != null) {
                predicates.add(builder.equal(root.get("createdBy"), createdBy));
            }
            if (startDate != null && endDate != null) {
                predicates.add(builder.between(root.get("date"), startDate, endDate));
            } else if (startDate != null) {
                predicates.add(builder.greaterThanOrEqualTo(root.get("date"), startDate));
            } else if (endDate != null) {
                predicates.add(builder.lessThanOrEqualTo(root.get("date"), endDate));
            }

            if (predicates.isEmpty()) {
                return builder.conjunction();
            }
            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
