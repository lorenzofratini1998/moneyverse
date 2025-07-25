package it.moneyverse.analytics.services.strategies;

import it.moneyverse.analytics.model.dto.AccountAnalyticsKpiDto;
import it.moneyverse.analytics.model.dto.AmountDto;
import it.moneyverse.analytics.model.dto.CountDto;
import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.projections.AccountAnalyticsKpiProjection;
import it.moneyverse.analytics.utils.AnalyticsUtils;
import org.springframework.stereotype.Component;

@Component
public class AccountAnalyticsKpiStrategy
    implements AnalyticsStrategy<AccountAnalyticsKpiDto, AccountAnalyticsKpiProjection> {

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
    return AnalyticsUtils.getAmount(current, p -> p.totalIncome().subtract(p.totalExpense()));
  }

  private AmountDto getAmount(
      AccountAnalyticsKpiProjection current, AccountAnalyticsKpiProjection compare) {
    return AnalyticsUtils.getAmount(
        current, compare, p -> p.totalIncome().subtract(p.totalExpense()));
  }

  private CountDto getCount(AccountAnalyticsKpiProjection current) {
    return AnalyticsUtils.getCount(current, null, AccountAnalyticsKpiProjection::activeAccounts);
  }

  private CountDto getCount(
      AccountAnalyticsKpiProjection current, AccountAnalyticsKpiProjection compare) {
    return AnalyticsUtils.getCount(current, compare, AccountAnalyticsKpiProjection::activeAccounts);
  }
}
