package it.moneyverse.analytics.services.strategies;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.moneyverse.analytics.model.dto.AccountAnalyticsDistributionDto;
import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.dto.PeriodDto;
import it.moneyverse.analytics.model.projections.AccountAnalyticsAmountDistributionProjection;
import it.moneyverse.analytics.utils.AnalyticsUtils;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountAnalyticsAmountDistributionCalculationStrategyTest {

  private AccountAnalyticsAmountDistributionStrategy strategy;

  @BeforeEach
  void setUp() {
    strategy = new AccountAnalyticsAmountDistributionStrategy();
  }

  @Test
  void testCalculate_WithCurrentAndCompareData_ShouldReturnDistributionWithComparison() {
    // Given
    UUID accountId = UUID.randomUUID();
    PeriodDto period = new PeriodDto(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
    PeriodDto comparePeriod = new PeriodDto(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));

    AccountAnalyticsAmountDistributionProjection currentProjection =
        mock(AccountAnalyticsAmountDistributionProjection.class);
    when(currentProjection.accountId()).thenReturn(accountId);
    when(currentProjection.totalIncome()).thenReturn(BigDecimal.valueOf(1000));
    when(currentProjection.totalExpense()).thenReturn(BigDecimal.valueOf(800));

    AccountAnalyticsAmountDistributionProjection compareProjection =
        mock(AccountAnalyticsAmountDistributionProjection.class);
    when(compareProjection.accountId()).thenReturn(accountId);
    when(compareProjection.totalIncome()).thenReturn(BigDecimal.valueOf(900));
    when(compareProjection.totalExpense()).thenReturn(BigDecimal.valueOf(700));

    FilterDto filterDto = mock(FilterDto.class);
    when(filterDto.period()).thenReturn(period);
    when(filterDto.comparePeriod()).thenReturn(comparePeriod);

    List<AccountAnalyticsAmountDistributionProjection> currentData = List.of(currentProjection);
    List<AccountAnalyticsAmountDistributionProjection> compareData = List.of(compareProjection);

    // Mock static method
    try (MockedStatic<AnalyticsUtils> analyticsUtilsMock = mockStatic(AnalyticsUtils.class)) {
      analyticsUtilsMock
          .when(() -> AnalyticsUtils.calculateTrend(any(BigDecimal.class), any(BigDecimal.class)))
          .thenReturn(BigDecimal.valueOf(0.11)); // 11% increase

      // When
      List<AccountAnalyticsDistributionDto> result =
          strategy.calculate(currentData, compareData, filterDto);

      // Then
      assertNotNull(result);
      assertEquals(1, result.size());

      AccountAnalyticsDistributionDto dto = result.getFirst();
      assertEquals(accountId, dto.getAccountId());
      assertEquals(period, dto.getPeriod());

      // Verify total income
      assertEquals(BigDecimal.valueOf(1000), dto.getTotalIncome().getAmount());
      assertEquals(BigDecimal.valueOf(0.11), dto.getTotalIncome().getVariation());

      // Verify total expense
      assertEquals(BigDecimal.valueOf(800), dto.getTotalExpense().getAmount());
      assertEquals(BigDecimal.valueOf(0.11), dto.getTotalExpense().getVariation());

      // Verify compare data is included
      assertNotNull(dto.getCompare());
      assertEquals(comparePeriod, dto.getCompare().getPeriod());
      assertEquals(accountId, dto.getCompare().getAccountId());
      assertEquals(BigDecimal.valueOf(900), dto.getCompare().getTotalIncome().getAmount());
      assertEquals(BigDecimal.valueOf(700), dto.getCompare().getTotalExpense().getAmount());
    }
  }

  @Test
  void testCalculate_WithCurrentDataOnly_ShouldReturnDistributionWithoutComparison() {
    // Given
    UUID accountId = UUID.randomUUID();
    PeriodDto period = new PeriodDto(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));

    AccountAnalyticsAmountDistributionProjection currentProjection =
        mock(AccountAnalyticsAmountDistributionProjection.class);
    when(currentProjection.accountId()).thenReturn(accountId);
    when(currentProjection.totalIncome()).thenReturn(BigDecimal.valueOf(1000));
    when(currentProjection.totalExpense()).thenReturn(BigDecimal.valueOf(800));

    FilterDto filterDto = mock(FilterDto.class);
    when(filterDto.period()).thenReturn(period);
    when(filterDto.comparePeriod()).thenReturn(null);

    List<AccountAnalyticsAmountDistributionProjection> currentData = List.of(currentProjection);
    List<AccountAnalyticsAmountDistributionProjection> compareData = Collections.emptyList();

    // When
    List<AccountAnalyticsDistributionDto> result =
        strategy.calculate(currentData, compareData, filterDto);

    // Then
    assertNotNull(result);
    assertEquals(1, result.size());

    AccountAnalyticsDistributionDto dto = result.getFirst();
    assertEquals(accountId, dto.getAccountId());
    assertEquals(period, dto.getPeriod());

    // Verify amounts without variation
    assertEquals(BigDecimal.valueOf(1000), dto.getTotalIncome().getAmount());
    assertNull(dto.getTotalIncome().getVariation());

    assertEquals(BigDecimal.valueOf(800), dto.getTotalExpense().getAmount());
    assertNull(dto.getTotalExpense().getVariation());

    // Verify no compare data
    assertNull(dto.getCompare());
  }

  @Test
  void testCalculate_WithZeroCompareAmount_ShouldNotCalculateVariation() {
    // Given
    UUID accountId = UUID.randomUUID();
    PeriodDto period = new PeriodDto(LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31));
    PeriodDto comparePeriod = new PeriodDto(LocalDate.of(2024, 1, 1), LocalDate.of(2024, 12, 31));

    AccountAnalyticsAmountDistributionProjection currentProjection =
        mock(AccountAnalyticsAmountDistributionProjection.class);
    when(currentProjection.accountId()).thenReturn(accountId);
    when(currentProjection.totalIncome()).thenReturn(BigDecimal.valueOf(1000));
    when(currentProjection.totalExpense()).thenReturn(BigDecimal.valueOf(800));

    AccountAnalyticsAmountDistributionProjection compareProjection =
        mock(AccountAnalyticsAmountDistributionProjection.class);
    when(compareProjection.accountId()).thenReturn(accountId);
    when(compareProjection.totalIncome()).thenReturn(BigDecimal.ZERO);
    when(compareProjection.totalExpense()).thenReturn(BigDecimal.ZERO);

    FilterDto filterDto = mock(FilterDto.class);
    when(filterDto.period()).thenReturn(period);
    when(filterDto.comparePeriod()).thenReturn(comparePeriod);

    List<AccountAnalyticsAmountDistributionProjection> currentData = List.of(currentProjection);
    List<AccountAnalyticsAmountDistributionProjection> compareData = List.of(compareProjection);

    // When
    List<AccountAnalyticsDistributionDto> result =
        strategy.calculate(currentData, compareData, filterDto);

    // Then
    AccountAnalyticsDistributionDto dto = result.getFirst();

    // When compare amount is zero, variation should be null
    assertNull(dto.getTotalIncome().getVariation());
    assertNull(dto.getTotalExpense().getVariation());
  }

  @Test
  void testCalculate_WithEmptyCurrentData_ShouldReturnEmptyResult() {
    // Given
    FilterDto filterDto = mock(FilterDto.class);
    List<AccountAnalyticsAmountDistributionProjection> currentData = Collections.emptyList();
    List<AccountAnalyticsAmountDistributionProjection> compareData = Collections.emptyList();

    // When
    List<AccountAnalyticsDistributionDto> result =
        strategy.calculate(currentData, compareData, filterDto);

    // Then
    assertNotNull(result);
    assertTrue(result.isEmpty());
  }
}
