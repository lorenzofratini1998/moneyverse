package it.moneyverse.analytics.services.strategies;

import it.moneyverse.analytics.model.dto.AccountAnalyticsKpiDto;
import it.moneyverse.analytics.model.dto.AmountDto;
import it.moneyverse.analytics.model.dto.CountDto;
import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.projections.AccountAnalyticsKpiProjection;
import java.math.BigDecimal;

import it.moneyverse.analytics.utils.AnalyticsUtils;
import org.springframework.stereotype.Component;

@Component
public class AccountAnalyticsKpiStrategy
    implements AccountAnalyticsStrategy<AccountAnalyticsKpiDto, AccountAnalyticsKpiProjection> {

  @Override
  public AccountAnalyticsKpiDto calculate(
      AccountAnalyticsKpiProjection currentData,
      AccountAnalyticsKpiProjection compareData,
      FilterDto parameters) {

    AccountAnalyticsKpiDto compare =
        parameters.comparePeriod() != null
            ? AccountAnalyticsKpiDto.builder()
                .withPeriod(parameters.comparePeriod())
                .withTotalAmount(getAmount(compareData))
                .withNumberOfActiveAccounts(getCount(compareData))
                .withMostUsedAccount(compareData.mostUsedAccount())
                .withLeastUsedAccount(compareData.leastUsedAccount())
                .build()
            : null;

    return AccountAnalyticsKpiDto.builder()
        .withPeriod(parameters.period())
        .withTotalAmount(getAmount(currentData, compareData))
        .withNumberOfActiveAccounts(getCount(currentData, compareData))
        .withMostUsedAccount(currentData.mostUsedAccount())
        .withLeastUsedAccount(currentData.leastUsedAccount())
        .withCompare(compare)
        .build();
  }

  private AmountDto getAmount(AccountAnalyticsKpiProjection current) {
    return getAmount(current, null);
  }

  private AmountDto getAmount(
      AccountAnalyticsKpiProjection current, AccountAnalyticsKpiProjection compare) {
    BigDecimal currentAmount = current.totalIncome().subtract(current.totalExpense());
    BigDecimal variation = null;
    if (compare != null) {
      BigDecimal compareAmount = compare.totalIncome().subtract(compare.totalExpense());
      if (compareAmount.compareTo(BigDecimal.ZERO) != 0) {
        variation = AnalyticsUtils.calculateTrend(currentAmount, compareAmount);
      }
    }
    return AmountDto.builder().withAmount(currentAmount).withVariation(variation).build();
  }

  private CountDto getCount(AccountAnalyticsKpiProjection current) {
    return getCount(current, null);
  }

  private CountDto getCount(
      AccountAnalyticsKpiProjection current, AccountAnalyticsKpiProjection compare) {
    Integer currentCount = current.activeAccounts();
    Integer compareCount = compare != null ? compare.activeAccounts() : null;
    Integer variation = null;

    if (compareCount != null && compareCount != 0) {
      variation = currentCount - compareCount;
    }

    return CountDto.builder().withCount(currentCount).withVariation(variation).build();
  }
}
