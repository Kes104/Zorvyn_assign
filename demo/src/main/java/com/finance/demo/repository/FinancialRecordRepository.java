package com.finance.demo.repository;
import com.finance.demo.model.FinancialRecord;
import com.finance.demo.model.RecordType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.repository.Query;

@Repository
public interface FinancialRecordRepository extends JpaRepository<FinancialRecord, Integer>, JpaSpecificationExecutor<FinancialRecord> {
    List<FinancialRecord> findByCreatedBy(Integer createdBy);
    List<FinancialRecord> findByType(RecordType type);
    List<FinancialRecord> findByCategory(String category);
    List<FinancialRecord> findByDateBetween(LocalDate start, LocalDate end);
    List<FinancialRecord> findByTypeAndCategoryAndDateBetween(
        RecordType type, String category, LocalDate startDate, LocalDate endDate);

    List<FinancialRecord> findTop10ByOrderByDateDescIdDesc();
    
    @Query("SELECT SUM(f.amount) FROM FinancialRecord f WHERE f.type = 'INCOME'")
    BigDecimal getTotalIncome();

    @Query("SELECT SUM(f.amount) FROM FinancialRecord f WHERE f.type = 'EXPENSE'")
    BigDecimal getTotalExpense();

    @Query("""
    SELECT 
        COALESCE(SUM(CASE WHEN f.type = 'INCOME' THEN f.amount ELSE 0 END), 0) -
        COALESCE(SUM(CASE WHEN f.type = 'EXPENSE' THEN f.amount ELSE 0 END), 0)
    FROM FinancialRecord f
    """)
    BigDecimal getBalance();

    @Query("SELECT f.category, SUM(f.amount) FROM FinancialRecord f GROUP BY f.category")
        List<Object[]> getSpendingByCategory();
}
