package it.moneyverse.analytics.services.strategies;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import it.moneyverse.analytics.model.dto.AccountAnalyticsKpiDto;
import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.dto.PeriodDto;
import it.moneyverse.analytics.model.projections.AccountAnalyticsKpiProjection;
import it.moneyverse.analytics.utils.AnalyticsUtils;
import java.math.BigDecimal;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AccountAnalyticsKpiCalculationStrategyTest {

  @InjectMocks private AccountAnalyticsKpiStrategy strategy;

  private AccountAnalyticsKpiProjection currentProjection;
  private AccountAnalyticsKpiProjection compareProjection;
  private FilterDto filterDto;
  private PeriodDto currentPeriod;
  private PeriodDto comparePeriod;

  @BeforeEach
  void setUp() {
    currentPeriod = mock(PeriodDto.class);
    comparePeriod = mock(PeriodDto.class);

    currentProjection = mock(AccountAnalyticsKpiProjection.class);
    compareProjection = mock(AccountAnalyticsKpiProjection.class);

    filterDto = mock(FilterDto.class);
    when(filterDto.period()).thenReturn(currentPeriod);
  }

  @Test
  void testCalculate_WithoutComparePeriod() {
    // Arrange
    when(filterDto.comparePeriod()).thenReturn(null);
    when(currentProjection.totalIncome()).thenReturn(BigDecimal.valueOf(1000));
    when(currentProjection.totalExpense()).thenReturn(BigDecimal.valueOf(400));
    when(currentProjection.activeAccounts()).thenReturn(5);
    when(currentProjection.mostUsedAccount()).thenReturn(UUID.randomUUID());
    when(currentProjection.leastUsedAccount()).thenReturn(UUID.randomUUID());

    // Act
    AccountAnalyticsKpiDto result = strategy.calculate(currentProjection, null, filterDto);

    // Assert
    assertNotNull(result);
    assertEquals(currentPeriod, result.getPeriod());
    assertNotNull(result.getTotalAmount());
    assertEquals(BigDecimal.valueOf(600), result.getTotalAmount().getAmount());
    assertNull(result.getTotalAmount().getVariation());
    assertNotNull(result.getNumberOfActiveAccounts());
    assertEquals(5, result.getNumberOfActiveAccounts().getCount());
    assertNull(result.getNumberOfActiveAccounts().getVariation());
    assertNull(result.getCompare());
  }

  @Test
  void testCalculate_WithComparePeriod() {
    // Arrange
    when(filterDto.comparePeriod()).thenReturn(comparePeriod);

    // Current data
    when(currentProjection.totalIncome()).thenReturn(BigDecimal.valueOf(1200));
    when(currentProjection.totalExpense()).thenReturn(BigDecimal.valueOf(300));
    when(currentProjection.activeAccounts()).thenReturn(7);
    UUID mostUsedAccount = UUID.randomUUID();
    UUID leastUsedAccount = UUID.randomUUID();
    when(currentProjection.mostUsedAccount()).thenReturn(mostUsedAccount);
    when(currentProjection.leastUsedAccount()).thenReturn(leastUsedAccount);

    // Compare data
    when(compareProjection.totalIncome()).thenReturn(BigDecimal.valueOf(800));
    when(compareProjection.totalExpense()).thenReturn(BigDecimal.valueOf(200));
    when(compareProjection.activeAccounts()).thenReturn(5);
    UUID compareMostUsedAccount = UUID.randomUUID();
    UUID compareLeastUsedAccount = UUID.randomUUID();
    when(compareProjection.mostUsedAccount()).thenReturn(compareMostUsedAccount);
    when(compareProjection.leastUsedAccount()).thenReturn(compareLeastUsedAccount);

    try (MockedStatic<AnalyticsUtils> mockedUtils = mockStatic(AnalyticsUtils.class)) {
      mockedUtils
          .when(() -> AnalyticsUtils.calculateTrend(any(BigDecimal.class), any(BigDecimal.class)))
          .thenReturn(BigDecimal.valueOf(0.5)); // 50% increase

      // Act
      AccountAnalyticsKpiDto result =
          strategy.calculate(currentProjection, compareProjection, filterDto);

      // Assert
      assertNotNull(result);
      assertEquals(currentPeriod, result.getPeriod());
      assertEquals(mostUsedAccount, result.getMostUsedAccount());
      assertEquals(leastUsedAccount, result.getLeastUsedAccount());

      // Check current amount calculation
      assertNotNull(result.getTotalAmount());
      assertEquals(BigDecimal.valueOf(900), result.getTotalAmount().getAmount()); // 1200 - 300
      assertEquals(BigDecimal.valueOf(0.5), result.getTotalAmount().getVariation());

      // Check current count calculation
      assertNotNull(result.getNumberOfActiveAccounts());
      assertEquals(7, result.getNumberOfActiveAccounts().getCount());
      assertEquals(2, result.getNumberOfActiveAccounts().getVariation()); // 7 - 5

      // Check compare data
      assertNotNull(result.getCompare());
      assertEquals(comparePeriod, result.getCompare().getPeriod());
      assertEquals(compareMostUsedAccount, result.getCompare().getMostUsedAccount());
      assertEquals(compareLeastUsedAccount, result.getCompare().getLeastUsedAccount());
      assertEquals(
          BigDecimal.valueOf(600), result.getCompare().getTotalAmount().getAmount()); // 800 - 200
      assertEquals(5, result.getCompare().getNumberOfActiveAccounts().getCount());

      // Verify AnalyticsUtils was called
      mockedUtils.verify(
          () -> AnalyticsUtils.calculateTrend(BigDecimal.valueOf(900), BigDecimal.valueOf(600)));
    }
  }

  @Test
  void testCalculate_WithComparePeriodAndZeroCompareAmount() {
    // Arrange
    when(filterDto.comparePeriod()).thenReturn(comparePeriod);

    // Current data
    when(currentProjection.totalIncome()).thenReturn(BigDecimal.valueOf(1000));
    when(currentProjection.totalExpense()).thenReturn(BigDecimal.valueOf(400));
    when(currentProjection.activeAccounts()).thenReturn(3);

    // Compare data with zero net amount
    when(compareProjection.totalIncome()).thenReturn(BigDecimal.valueOf(500));
    when(compareProjection.totalExpense()).thenReturn(BigDecimal.valueOf(500));
    when(compareProjection.activeAccounts()).thenReturn(2);

    try (MockedStatic<AnalyticsUtils> mockedUtils = mockStatic(AnalyticsUtils.class)) {
      // Act
      AccountAnalyticsKpiDto result =
          strategy.calculate(currentProjection, compareProjection, filterDto);

      // Assert
      assertNotNull(result);
      assertNotNull(result.getTotalAmount());
      assertEquals(BigDecimal.valueOf(600), result.getTotalAmount().getAmount());
      assertNull(result.getTotalAmount().getVariation());

      // Verify AnalyticsUtils was not called due to zero compare amount
      mockedUtils.verifyNoInteractions();
    }
  }

  @Test
  void testCalculate_WithComparePeriodAndZeroCompareCount() {
    // Arrange
    when(filterDto.comparePeriod()).thenReturn(comparePeriod);

    // Current data
    when(currentProjection.totalIncome()).thenReturn(BigDecimal.valueOf(1000));
    when(currentProjection.totalExpense()).thenReturn(BigDecimal.valueOf(400));
    when(currentProjection.activeAccounts()).thenReturn(5);

    // Compare data
    when(compareProjection.totalIncome()).thenReturn(BigDecimal.valueOf(800));
    when(compareProjection.totalExpense()).thenReturn(BigDecimal.valueOf(300));
    when(compareProjection.activeAccounts()).thenReturn(0); // Zero active accounts

    try (MockedStatic<AnalyticsUtils> mockedUtils = mockStatic(AnalyticsUtils.class)) {
      mockedUtils
          .when(() -> AnalyticsUtils.calculateTrend(any(BigDecimal.class), any(BigDecimal.class)))
          .thenReturn(BigDecimal.valueOf(0.2));

      // Act
      AccountAnalyticsKpiDto result =
          strategy.calculate(currentProjection, compareProjection, filterDto);

      // Assert
      assertNotNull(result);
      assertNotNull(result.getNumberOfActiveAccounts());
      assertEquals(5, result.getNumberOfActiveAccounts().getCount());
      assertNull(
          result
              .getNumberOfActiveAccounts()
              .getVariation()); // Should be null when compare count is zero
    }
  }

  @Test
  void testCalculate_WithNegativeAmounts() {
    // Arrange
    when(filterDto.comparePeriod()).thenReturn(comparePeriod);

    // Current data (expense > income)
    when(currentProjection.totalIncome()).thenReturn(BigDecimal.valueOf(300));
    when(currentProjection.totalExpense()).thenReturn(BigDecimal.valueOf(800));
    when(currentProjection.activeAccounts()).thenReturn(2);

    // Compare data
    when(compareProjection.totalIncome()).thenReturn(BigDecimal.valueOf(400));
    when(compareProjection.totalExpense()).thenReturn(BigDecimal.valueOf(600));
    when(compareProjection.activeAccounts()).thenReturn(3);

    try (MockedStatic<AnalyticsUtils> mockedUtils = mockStatic(AnalyticsUtils.class)) {
      mockedUtils
          .when(() -> AnalyticsUtils.calculateTrend(any(BigDecimal.class), any(BigDecimal.class)))
          .thenReturn(BigDecimal.valueOf(-1.5)); // Negative trend

      // Act
      AccountAnalyticsKpiDto result =
          strategy.calculate(currentProjection, compareProjection, filterDto);

      // Assert
      assertNotNull(result);
      assertNotNull(result.getTotalAmount());
      assertEquals(BigDecimal.valueOf(-500), result.getTotalAmount().getAmount());
      assertEquals(BigDecimal.valueOf(-1.5), result.getTotalAmount().getVariation());

      assertNotNull(result.getNumberOfActiveAccounts());
      assertEquals(2, result.getNumberOfActiveAccounts().getCount());
      assertEquals(-1, result.getNumberOfActiveAccounts().getVariation());

      // Verify compare data
      assertNotNull(result.getCompare());
      assertEquals(BigDecimal.valueOf(-200), result.getCompare().getTotalAmount().getAmount());
    }
  }

  @Test
  void testCalculate_VerifyBuilderPatternUsage() {
    // Arrange
    when(filterDto.comparePeriod()).thenReturn(null);
    when(currentProjection.totalIncome()).thenReturn(BigDecimal.valueOf(1000));
    when(currentProjection.totalExpense()).thenReturn(BigDecimal.valueOf(400));
    when(currentProjection.activeAccounts()).thenReturn(5);
    UUID mostUsed = UUID.randomUUID();
    UUID leastUsed = UUID.randomUUID();
    when(currentProjection.mostUsedAccount()).thenReturn(mostUsed);
    when(currentProjection.leastUsedAccount()).thenReturn(leastUsed);

    // Act
    AccountAnalyticsKpiDto result = strategy.calculate(currentProjection, null, filterDto);

    assertNotNull(result);
    assertEquals(currentPeriod, result.getPeriod());
    assertEquals(mostUsed, result.getMostUsedAccount());
    assertEquals(leastUsed, result.getLeastUsedAccount());
    assertNotNull(result.getTotalAmount());
    assertNotNull(result.getNumberOfActiveAccounts());
    assertNull(result.getCompare());
  }
}
