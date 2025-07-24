package it.moneyverse.analytics.services.strategies;

import it.moneyverse.analytics.model.dto.AmountDto;
import it.moneyverse.analytics.model.dto.CategoryAnalyticsDistributionDto;
import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.projections.CategoryAnalyticsDistributionProjection;
import it.moneyverse.analytics.utils.AnalyticsUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CategoryAnalyticsDistributionStrategy
    implements AnalyticsStrategy<
        List<CategoryAnalyticsDistributionDto>, List<CategoryAnalyticsDistributionProjection>> {

  @Override
  public List<CategoryAnalyticsDistributionDto> calculate(
      List<CategoryAnalyticsDistributionProjection> currentData,
      List<CategoryAnalyticsDistributionProjection> compareData,
      FilterDto parameters) {
    List<CategoryAnalyticsDistributionDto> result = new ArrayList<>();

    for (CategoryAnalyticsDistributionProjection current : currentData) {
      UUID categoryId = current.categoryId();
      CategoryAnalyticsDistributionProjection compare = findByCategoryId(compareData, categoryId);

      CategoryAnalyticsDistributionDto compareDto =
          (parameters.comparePeriod() != null && compare != null)
              ? CategoryAnalyticsDistributionDto.builder()
                  .withPeriod(parameters.comparePeriod())
                  .withCategoryId(categoryId)
                  .withTotalIncome(getAmount(compare.totalIncome()))
                  .withTotalExpense(getAmount(compare.totalExpense()))
                  .withTotalAmount(getAmount(compare.totalAmount()))
                  .build()
              : null;

      CategoryAnalyticsDistributionDto dto =
          CategoryAnalyticsDistributionDto.builder()
              .withPeriod(parameters.period())
              .withCategoryId(categoryId)
              .withTotalIncome(
                  getAmount(current.totalIncome(), compare != null ? compare.totalIncome() : null))
              .withTotalExpense(
                  getAmount(
                      current.totalExpense(), compare != null ? compare.totalExpense() : null))
              .withTotalAmount(
                  getAmount(current.totalAmount(), compare != null ? compare.totalAmount() : null))
              .withCompare(compareDto)
              .build();

      result.add(dto);
    }

    return result;
  }

  private CategoryAnalyticsDistributionProjection findByCategoryId(
      List<CategoryAnalyticsDistributionProjection> data, UUID categoryId) {
    if (data == null) return null;
    return data.stream().filter(p -> p.categoryId().equals(categoryId)).findFirst().orElse(null);
  }

  private AmountDto getAmount(BigDecimal currentAmount) {
    return getAmount(currentAmount, null);
  }

  private AmountDto getAmount(BigDecimal currentAmount, BigDecimal compareAmount) {
    BigDecimal variation = null;
    if (compareAmount != null) {
      if (compareAmount.compareTo(BigDecimal.ZERO) != 0) {
        variation = AnalyticsUtils.calculateTrend(currentAmount, compareAmount);
      }
    }
    return AmountDto.builder().withAmount(currentAmount).withVariation(variation).build();
  }
}
