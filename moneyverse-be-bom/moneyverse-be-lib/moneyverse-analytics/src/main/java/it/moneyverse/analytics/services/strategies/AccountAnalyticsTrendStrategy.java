package it.moneyverse.analytics.services.strategies;

import it.moneyverse.analytics.model.dto.*;
import it.moneyverse.analytics.model.projections.AccountAnalyticsTrendProjection;
import it.moneyverse.analytics.utils.AnalyticsUtils;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AccountAnalyticsTrendStrategy
    implements AnalyticsStrategy<
        List<AccountAnalyticsTrendDto>, List<AccountAnalyticsTrendProjection>> {

  @Override
  public List<AccountAnalyticsTrendDto> calculate(
      List<AccountAnalyticsTrendProjection> currentData,
      List<AccountAnalyticsTrendProjection> compareData,
      FilterDto parameters) {

    if (currentData == null || currentData.isEmpty()) {
      return Collections.emptyList();
    }

    Map<UUID, List<AccountAnalyticsTrendProjection>> currentDataMap = groupByAccountId(currentData);
    Map<UUID, List<AccountAnalyticsTrendProjection>> compareDataMap = groupByAccountId(compareData);

    return currentDataMap.keySet().stream()
        .map(
            accountId ->
                buildAccountTrendDto(accountId, currentDataMap, compareDataMap, parameters))
        .collect(Collectors.toList());
  }

  private AccountAnalyticsTrendDto buildAccountTrendDto(
      UUID accountId,
      Map<UUID, List<AccountAnalyticsTrendProjection>> currentDataMap,
      Map<UUID, List<AccountAnalyticsTrendProjection>> compareDataMap,
      FilterDto parameters) {

    List<AccountAnalyticsTrendProjection> currentAccountData = currentDataMap.get(accountId);
    List<AccountAnalyticsTrendProjection> compareAccountData =
        compareDataMap.getOrDefault(accountId, Collections.emptyList());

    AccountAnalyticsTrendDto compareDto =
        buildCompareDtoIfNeeded(accountId, compareAccountData, parameters);
    List<ExpenseIncomeDto> currentAmounts =
        buildCurrentAmountsWithVariations(currentAccountData, compareAccountData);

    return AccountAnalyticsTrendDto.builder()
        .withPeriod(parameters.period())
        .withAccountId(accountId)
        .withData(currentAmounts)
        .withCompare(compareDto)
        .build();
  }

  private AccountAnalyticsTrendDto buildCompareDtoIfNeeded(
      UUID accountId,
      List<AccountAnalyticsTrendProjection> compareAccountData,
      FilterDto parameters) {

    if (parameters.comparePeriod() == null || compareAccountData.isEmpty()) {
      return null;
    }

    List<ExpenseIncomeDto> compareAmounts =
        compareAccountData.stream()
            .map(this::convertProjectionToExpenseIncomeDto)
            .collect(Collectors.toList());

    return AccountAnalyticsTrendDto.builder()
        .withPeriod(parameters.comparePeriod())
        .withAccountId(accountId)
        .withData(compareAmounts)
        .build();
  }

  private List<ExpenseIncomeDto> buildCurrentAmountsWithVariations(
      List<AccountAnalyticsTrendProjection> currentAccountData,
      List<AccountAnalyticsTrendProjection> compareAccountData) {

    List<ExpenseIncomeDto> amounts = new ArrayList<>();

    for (int i = 0; i < currentAccountData.size(); i++) {
      AccountAnalyticsTrendProjection current = currentAccountData.get(i);
      AccountAnalyticsTrendProjection compare =
          i < compareAccountData.size() ? compareAccountData.get(i) : null;

      amounts.add(createExpenseIncomeDtoWithVariation(current, compare));
    }

    return amounts;
  }

  private ExpenseIncomeDto createExpenseIncomeDtoWithVariation(
      AccountAnalyticsTrendProjection current, AccountAnalyticsTrendProjection compare) {

    BigDecimal currentExpense =
        current.totalExpense() != null ? current.totalExpense() : BigDecimal.ZERO;
    BigDecimal currentIncome =
        current.totalIncome() != null ? current.totalIncome() : BigDecimal.ZERO;
    BigDecimal currentTotal =
        current.totalAmount() != null ? current.totalAmount() : BigDecimal.ZERO;

    BigDecimal compareExpense =
        compare != null && compare.totalExpense() != null ? compare.totalExpense() : null;
    BigDecimal compareIncome =
        compare != null && compare.totalIncome() != null ? compare.totalIncome() : null;
    BigDecimal compareTotal =
        compare != null && compare.totalAmount() != null ? compare.totalAmount() : null;

    BigDecimal expenseVariation = AnalyticsUtils.calculateTrend(currentExpense, compareExpense);
    BigDecimal incomeVariation = AnalyticsUtils.calculateTrend(currentIncome, compareIncome);
    BigDecimal totalVariation = AnalyticsUtils.calculateTrend(currentTotal, compareTotal);

    return ExpenseIncomeDto.builder()
        .withPeriod(new PeriodDto(current.startDate(), current.endDate()))
        .withExpense(buildAmountDto(currentExpense, expenseVariation))
        .withIncome(buildAmountDto(currentIncome, incomeVariation))
        .withTotal(buildAmountDto(currentTotal, totalVariation))
        .build();
  }

  private ExpenseIncomeDto convertProjectionToExpenseIncomeDto(
      AccountAnalyticsTrendProjection projection) {
    return ExpenseIncomeDto.builder()
        .withPeriod(new PeriodDto(projection.startDate(), projection.endDate()))
        .withExpense(buildAmountDto(projection.totalExpense(), null))
        .withIncome(buildAmountDto(projection.totalIncome(), null))
        .withTotal(buildAmountDto(projection.totalAmount(), null))
        .build();
  }

  private AmountDto buildAmountDto(BigDecimal amount, BigDecimal variation) {
    return AmountDto.builder().withAmount(amount).withVariation(variation).build();
  }

  private Map<UUID, List<AccountAnalyticsTrendProjection>> groupByAccountId(
      List<AccountAnalyticsTrendProjection> data) {
    return data == null
        ? Collections.emptyMap()
        : data.stream().collect(Collectors.groupingBy(AccountAnalyticsTrendProjection::accountId));
  }
}
