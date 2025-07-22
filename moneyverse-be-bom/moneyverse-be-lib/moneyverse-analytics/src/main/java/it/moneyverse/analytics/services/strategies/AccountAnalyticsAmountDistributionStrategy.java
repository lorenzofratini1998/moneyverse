package it.moneyverse.analytics.services.strategies;

import it.moneyverse.analytics.model.dto.AccountAnalyticsDistributionDto;
import it.moneyverse.analytics.model.dto.AmountDto;
import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.projections.AccountAnalyticsAmountDistributionProjection;
import it.moneyverse.analytics.utils.AnalyticsUtils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class AccountAnalyticsAmountDistributionStrategy
    implements AccountAnalyticsStrategy<
        List<AccountAnalyticsDistributionDto>, List<AccountAnalyticsAmountDistributionProjection>> {

  @Override
  public List<AccountAnalyticsDistributionDto> calculate(
      List<AccountAnalyticsAmountDistributionProjection> currentData,
      List<AccountAnalyticsAmountDistributionProjection> compareData,
      FilterDto parameters) {

    List<AccountAnalyticsDistributionDto> result = new ArrayList<>();

    for (AccountAnalyticsAmountDistributionProjection current : currentData) {
      UUID accountId = current.accountId();
      AccountAnalyticsAmountDistributionProjection compare =
          findByAccountId(compareData, accountId);

      AccountAnalyticsDistributionDto compareDto =
          (parameters.comparePeriod() != null && compare != null)
              ? AccountAnalyticsDistributionDto.builder()
                  .withPeriod(parameters.comparePeriod())
                  .withAccountId(accountId)
                  .withTotalIncome(getAmount(compare.totalIncome()))
                  .withTotalExpense(getAmount(compare.totalExpense()))
                  .build()
              : null;

      AccountAnalyticsDistributionDto dto =
          AccountAnalyticsDistributionDto.builder()
              .withPeriod(parameters.period())
              .withAccountId(accountId)
              .withTotalIncome(
                  getAmount(current.totalIncome(), compare != null ? compare.totalIncome() : null))
              .withTotalExpense(
                  getAmount(
                      current.totalExpense(), compare != null ? compare.totalExpense() : null))
              .withCompare(compareDto)
              .build();

      result.add(dto);
    }

    return result;
  }

  private AccountAnalyticsAmountDistributionProjection findByAccountId(
      List<AccountAnalyticsAmountDistributionProjection> data, UUID accountId) {
    if (data == null) return null;
    return data.stream().filter(p -> p.accountId().equals(accountId)).findFirst().orElse(null);
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
