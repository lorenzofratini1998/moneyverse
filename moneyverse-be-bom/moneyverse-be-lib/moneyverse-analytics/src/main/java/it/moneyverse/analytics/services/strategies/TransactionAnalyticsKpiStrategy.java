package it.moneyverse.analytics.services.strategies;

import it.moneyverse.analytics.model.dto.CountDto;
import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.dto.TransactionAnalyticsKpiDto;
import it.moneyverse.analytics.model.projections.TransactionAnalyticsKpiProjection;
import it.moneyverse.analytics.utils.AnalyticsUtils;
import org.springframework.stereotype.Component;

@Component
public class TransactionAnalyticsKpiStrategy
    implements AnalyticsStrategy<TransactionAnalyticsKpiDto, TransactionAnalyticsKpiProjection> {

  @Override
  public TransactionAnalyticsKpiDto calculate(
      TransactionAnalyticsKpiProjection currentData,
      TransactionAnalyticsKpiProjection compareData,
      FilterDto parameters) {

    TransactionAnalyticsKpiDto compare =
        parameters.comparePeriod() != null
            ? TransactionAnalyticsKpiDto.builder()
                .withPeriod(parameters.comparePeriod())
                .withNumberOfTransactions(getCount(compareData))
                .withTotalIncome(
                    AnalyticsUtils.getAmount(
                        compareData, TransactionAnalyticsKpiProjection::totalIncome))
                .withTotalExpense(
                    AnalyticsUtils.getAmount(
                        compareData, TransactionAnalyticsKpiProjection::totalExpense))
                .withAverageAmount(
                    AnalyticsUtils.getAmount(
                        compareData, TransactionAnalyticsKpiProjection::averageAmount))
                .withQuantile90(
                    AnalyticsUtils.getAmount(
                        compareData, TransactionAnalyticsKpiProjection::quantile90))
                .build()
            : null;

    return TransactionAnalyticsKpiDto.builder()
        .withPeriod(parameters.period())
        .withNumberOfTransactions(getCount(currentData, compareData))
        .withTotalIncome(
            AnalyticsUtils.getAmount(
                currentData, compareData, TransactionAnalyticsKpiProjection::totalIncome))
        .withTotalExpense(
            AnalyticsUtils.getAmount(
                currentData, compareData, TransactionAnalyticsKpiProjection::totalExpense))
        .withAverageAmount(
            AnalyticsUtils.getAmount(
                currentData, compareData, TransactionAnalyticsKpiProjection::averageAmount))
        .withQuantile90(
            AnalyticsUtils.getAmount(
                currentData, compareData, TransactionAnalyticsKpiProjection::quantile90))
        .withCompare(compare)
        .build();
  }

  private CountDto getCount(TransactionAnalyticsKpiProjection current) {
    return AnalyticsUtils.getCount(
        current, TransactionAnalyticsKpiProjection::numberOfTransactions);
  }

  private CountDto getCount(
      TransactionAnalyticsKpiProjection current, TransactionAnalyticsKpiProjection compare) {
    return AnalyticsUtils.getCount(
        current, compare, TransactionAnalyticsKpiProjection::numberOfTransactions);
  }
}
