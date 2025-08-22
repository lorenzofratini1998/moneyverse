package it.moneyverse.analytics.services.strategies;

import it.moneyverse.analytics.model.dto.*;
import it.moneyverse.analytics.model.projections.CategoryAnalyticsTrendProjection;
import it.moneyverse.analytics.utils.AnalyticsUtils;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CategoryAnalyticsTrendStrategy
    implements AnalyticsStrategy<
        List<CategoryAnalyticsTrendDto>, List<CategoryAnalyticsTrendProjection>> {
  @Override
  public List<CategoryAnalyticsTrendDto> calculate(
      List<CategoryAnalyticsTrendProjection> currentData,
      List<CategoryAnalyticsTrendProjection> compareData,
      FilterDto parameters) {

    if (currentData == null || currentData.isEmpty()) {
      return Collections.emptyList();
    }

    Map<UUID, List<CategoryAnalyticsTrendProjection>> currentDataMap =
        groupByCategoryId(currentData);
    Map<UUID, List<CategoryAnalyticsTrendProjection>> compareDataMap =
        groupByCategoryId(compareData);

    return currentDataMap.keySet().stream()
        .map(
            categoryId ->
                buildCategoryTrendDto(categoryId, currentDataMap, compareDataMap, parameters))
        .collect(Collectors.toList());
  }

  private CategoryAnalyticsTrendDto buildCategoryTrendDto(
      UUID categoryId,
      Map<UUID, List<CategoryAnalyticsTrendProjection>> currentDataMap,
      Map<UUID, List<CategoryAnalyticsTrendProjection>> compareDataMap,
      FilterDto parameters) {
    List<CategoryAnalyticsTrendProjection> currentData = currentDataMap.get(categoryId);
    List<CategoryAnalyticsTrendProjection> compareData =
        compareDataMap.getOrDefault(categoryId, Collections.emptyList());

    CategoryAnalyticsTrendDto compareDto =
        buildCompareDtoIfNeeded(categoryId, compareData, parameters);
    List<ExpenseIncomeDto> currentAmounts =
        buildCurrentAmountsWithVariations(currentData, compareData);

    return CategoryAnalyticsTrendDto.builder()
        .withPeriod(parameters.period())
        .withCategoryId(categoryId)
        .withData(currentAmounts)
        .withCompare(compareDto)
        .build();
  }

  private CategoryAnalyticsTrendDto buildCompareDtoIfNeeded(
      UUID categoryId, List<CategoryAnalyticsTrendProjection> compareData, FilterDto parameters) {
    if (parameters.comparePeriod() == null || compareData.isEmpty()) {
      return null;
    }

    List<ExpenseIncomeDto> compareAmounts =
        compareData.stream()
            .map(this::convertProjectionToExpenseIncomeDto)
            .collect(Collectors.toList());

    return CategoryAnalyticsTrendDto.builder()
        .withPeriod(parameters.comparePeriod())
        .withCategoryId(categoryId)
        .withData(compareAmounts)
        .build();
  }

  private List<ExpenseIncomeDto> buildCurrentAmountsWithVariations(
      List<CategoryAnalyticsTrendProjection> currentData,
      List<CategoryAnalyticsTrendProjection> compareData) {
    List<ExpenseIncomeDto> amounts = new ArrayList<>();

    for (int i = 0; i < currentData.size(); i++) {
      CategoryAnalyticsTrendProjection current = currentData.get(i);
      CategoryAnalyticsTrendProjection compare = i < compareData.size() ? compareData.get(i) : null;

      amounts.add(createExpenseIncomeDtoWithVariation(current, compare));
    }

    return amounts;
  }

  private ExpenseIncomeDto createExpenseIncomeDtoWithVariation(
      CategoryAnalyticsTrendProjection current, CategoryAnalyticsTrendProjection compare) {
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
      CategoryAnalyticsTrendProjection projection) {
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

  private Map<UUID, List<CategoryAnalyticsTrendProjection>> groupByCategoryId(
      List<CategoryAnalyticsTrendProjection> data) {
    return data == null
        ? Collections.emptyMap()
        : data.stream()
            .collect(Collectors.groupingBy(CategoryAnalyticsTrendProjection::categoryId));
  }
}
