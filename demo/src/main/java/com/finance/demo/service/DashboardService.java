package com.finance.demo.service;

import com.finance.demo.dto.DashboardSummaryResponse;
import com.finance.demo.dto.FinancialRecordResponse;
import com.finance.demo.dto.TrendPoint;
import com.finance.demo.exception.BadRequestException;
import com.finance.demo.model.FinancialRecord;
import com.finance.demo.model.RecordType;
import com.finance.demo.repository.FinancialRecordRepository;
import com.finance.demo.repository.FinancialRecordSpecifications;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final FinancialRecordRepository repository;

    public DashboardService(FinancialRecordRepository repository) {
        this.repository = repository;
    }

    public DashboardSummaryResponse getSummary(LocalDate start, LocalDate end, Integer createdBy) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
        List<FinancialRecord> records = repository.findAll(
                FinancialRecordSpecifications.withFilters(null, null, start, end, createdBy)
        );

        BigDecimal totalIncome = sum(records, RecordType.INCOME);
        BigDecimal totalExpense = sum(records, RecordType.EXPENSE);
        BigDecimal net = totalIncome.subtract(totalExpense);

        Map<String, BigDecimal> categoryTotals = records.stream()
                .collect(Collectors.groupingBy(
                        FinancialRecord::getCategory,
                        LinkedHashMap::new,
                        Collectors.mapping(
                                FinancialRecord::getAmount,
                                Collectors.reducing(BigDecimal.ZERO, BigDecimal::add)
                        )));

        List<FinancialRecordResponse> recent = records.stream()
                .sorted(Comparator.comparing(FinancialRecord::getDate).reversed()
                        .thenComparing(FinancialRecord::getId, Comparator.nullsLast(Comparator.reverseOrder())))
                .limit(10)
                .map(this::toResponse)
                .collect(Collectors.toList());

        return new DashboardSummaryResponse(totalIncome, totalExpense, net, categoryTotals, recent);
    }

    public List<TrendPoint> getMonthlyTrends(int months, Integer createdBy) {
        int cappedMonths = Math.max(1, Math.min(months, 24));
        YearMonth current = YearMonth.now();
        YearMonth startMonth = current.minusMonths(cappedMonths - 1L);
        LocalDate startDate = startMonth.atDay(1);
        LocalDate endDate = current.atEndOfMonth();

        List<FinancialRecord> records = repository.findAll(
                FinancialRecordSpecifications.withFilters(null, null, startDate, endDate, createdBy)
        );

        Map<YearMonth, List<FinancialRecord>> grouped = records.stream()
                .collect(Collectors.groupingBy(r -> YearMonth.from(r.getDate())));

        List<TrendPoint> points = new ArrayList<>();
        YearMonth cursor = startMonth;
        while (!cursor.isAfter(current)) {
            List<FinancialRecord> bucket = grouped.getOrDefault(cursor, List.of());
            BigDecimal income = sum(bucket, RecordType.INCOME);
            BigDecimal expense = sum(bucket, RecordType.EXPENSE);
            points.add(new TrendPoint(cursor.toString(), income, expense, income.subtract(expense)));
            cursor = cursor.plusMonths(1);
        }
        return points;
    }

    private BigDecimal sum(List<FinancialRecord> records, RecordType type) {
        return records.stream()
                .filter(record -> record.getType() == type)
                .map(FinancialRecord::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private FinancialRecordResponse toResponse(FinancialRecord record) {
        return new FinancialRecordResponse(
                record.getId(),
                record.getCreatedBy(),
                record.getType(),
                record.getCategory(),
                record.getAmount(),
                record.getDate(),
                record.getNotes()
        );
    }
}
