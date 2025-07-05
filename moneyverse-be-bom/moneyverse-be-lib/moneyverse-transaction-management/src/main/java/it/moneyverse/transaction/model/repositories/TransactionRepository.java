package it.moneyverse.transaction.model.repositories;

import it.moneyverse.transaction.model.dto.projection.CategoryMonthlyStatsProjection;
import it.moneyverse.transaction.model.dto.projection.CategoryTotalsAndCountsProjection;
import it.moneyverse.transaction.model.entities.Transaction;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface TransactionRepository
    extends JpaRepository<Transaction, UUID>, TransactionCustomRepository {
  boolean existsByUserIdAndTransactionId(UUID userId, UUID transactionId);

  List<Transaction> findTransactionByUserId(UUID userId);

  List<Transaction> findTransactionByAccountId(UUID accountId);

  List<Transaction> findTransactionByCategoryId(UUID categoryId);

  List<Transaction> findTransactionByBudgetId(UUID budgetId);

  @Query(
      """
                  SELECT
                      COALESCE(SUM(CASE
                                      WHEN t.date BETWEEN :start AND :end
                                      THEN t.normalizedAmount
                                   END), 0) AS currentTotal,
                      COALESCE(COUNT(DISTINCT (
                                    CASE
                                        WHEN t.date BETWEEN :start AND :end
                                        THEN t.categoryId
                                    END)), 0) AS currentActiveCategoryCount,
                      COALESCE(SUM(CASE
                                      WHEN t.date BETWEEN :previousStart AND :previousEnd
                                      THEN t.normalizedAmount
                                   END), 0) AS previousTotal,
                      COALESCE(COUNT(DISTINCT (
                                    CASE
                                      WHEN :previousStart IS NOT NULL
                                       AND t.date BETWEEN :previousStart AND :previousEnd
                                      THEN t.categoryId
                                    END)), 0) AS previousActiveCategoryCount
                  FROM Transaction t
                  WHERE t.userId = :userId
                    AND (
                      :#{#accounts == null || #accounts.isEmpty()} = TRUE
                      OR t.accountId IN :accounts
                    )
                    AND (
                     :#{#categories == null || #categories.isEmpty()} = TRUE
                     OR t.categoryId IN :categories
                   )
                    AND (
                        t.date BETWEEN :start AND :end
                    OR (
                         :previousStart IS NOT NULL
                         AND t.date BETWEEN :previousStart AND :previousEnd)
                  )
              """)
  CategoryTotalsAndCountsProjection getTotalsAndCategoryCountsByUserIdAndPeriod(
      UUID userId,
      List<UUID> accounts,
      List<UUID> categories,
      LocalDate start,
      LocalDate end,
      LocalDate previousStart,
      LocalDate previousEnd);

  @Query(
      """
          SELECT
            t.categoryId                  AS categoryId,
            YEAR(t.date)                  AS year,
            MONTH(t.date)                 AS month,
            SUM(t.normalizedAmount)       AS total
          FROM Transaction t
          WHERE t.userId      = :userId
            AND (
              :#{#accounts == null || #accounts.isEmpty()} = TRUE
              OR t.accountId IN :accounts
            )
            AND (
             :#{#categories == null || #categories.isEmpty()} = TRUE
             OR t.categoryId IN :categories
           )
            AND t.date BETWEEN :start AND :end
          GROUP BY
            t.categoryId, YEAR(t.date), MONTH(t.date)
          ORDER BY
            t.categoryId, YEAR(t.date), MONTH(t.date)
          """)
  List<CategoryMonthlyStatsProjection> getMonthlyTotalsByCategory(
      UUID userId, List<UUID> accounts, List<UUID> categories, LocalDate start, LocalDate end);
}
