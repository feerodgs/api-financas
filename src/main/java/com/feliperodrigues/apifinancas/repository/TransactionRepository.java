package com.feliperodrigues.apifinancas.repository;

import com.feliperodrigues.apifinancas.domain.Transaction;
import com.feliperodrigues.apifinancas.domain.enums.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    @Query("""
            SELECT t FROM Transaction t
            WHERE t.user.id = :userId
              AND (:accountId IS NULL OR t.account.id = :accountId)
              AND (:categoryId IS NULL OR t.category.id = :categoryId)
              AND (:startDate IS NULL OR t.date >= :startDate)
              AND (:endDate IS NULL OR t.date <= :endDate)
            """)
    Page<Transaction> findFiltered(
            @Param("userId") Long userId,
            @Param("accountId") Long accountId,
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable
    );

    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.user.id = :userId
              AND t.date >= :startDate
              AND t.date <= :endDate
              AND t.type = :type
            """)
    BigDecimal sumByTypeAndPeriod(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") TransactionType type
    );

    @Query("""
            SELECT t.category.id, t.category.name, t.type, SUM(t.amount)
            FROM Transaction t
            WHERE t.user.id = :userId
              AND t.date >= :startDate
              AND t.date <= :endDate
              AND t.category IS NOT NULL
            GROUP BY t.category.id, t.category.name, t.type
            ORDER BY SUM(t.amount) DESC
            """)
    List<Object[]> findCategoryBreakdown(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );
}
