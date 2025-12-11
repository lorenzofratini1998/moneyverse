package it.moneyverse.analytics.services.strategies;

import it.moneyverse.analytics.model.dto.*;
import it.moneyverse.analytics.model.projections.TransactionAnalyticsTrendProjection;
import it.moneyverse.analytics.utils.AnalyticsUtils;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TransactionAnalyticsTrendStrategy
    implements AnalyticsStrategy<
        TransactionAnalyticsTrendDto, List<TransactionAnalyticsTrendProjection>> {

  @Override
  public TransactionAnalyticsTrendDto calculate(
      List<TransactionAnalyticsTrendProjection> currentData,
      List<TransactionAnalyticsTrendProjection> compareData,
      FilterDto parameters) {

    Map<PeriodDto, TransactionAnalyticsTrendProjection> compareMap =
        Optional.ofNullable(compareData).orElse(List.of()).stream()
            .collect(
                Collectors.toMap(
                    p -> new PeriodDto(p.startDate(), p.endDate()), Function.identity()));

    List<ExpenseIncomeDto> currentItems =
        currentData.stream()
            .map(
                current -> {
                  PeriodDto currentPeriod = new PeriodDto(current.startDate(), current.endDate());
                  return ExpenseIncomeDto.builder()
                      .withPeriod(currentPeriod)
                      .withExpense(
                          AnalyticsUtils.getAmount(
                              current,
                              compareMap.get(currentPeriod),
                              TransactionAnalyticsTrendProjection::expense))
                      .withIncome(
                          AnalyticsUtils.getAmount(
                              current,
                              compareMap.get(currentPeriod),
                              TransactionAnalyticsTrendProjection::income))
                      .build();
                })
            .toList();

    List<ExpenseIncomeDto> compareItems =
        Optional.ofNullable(compareData).orElse(List.of()).stream()
            .map(
                current -> {
                  PeriodDto currentPeriod = new PeriodDto(current.startDate(), current.endDate());
                  return ExpenseIncomeDto.builder()
                      .withPeriod(currentPeriod)
                      .withExpense(
                          AnalyticsUtils.getAmount(
                              current, TransactionAnalyticsTrendProjection::expense))
                      .withIncome(
                          AnalyticsUtils.getAmount(
                              current, TransactionAnalyticsTrendProjection::income))
                      .build();
                })
            .toList();

    TransactionAnalyticsTrendDto compareTrend =
        (parameters.comparePeriod() != null && !compareItems.isEmpty())
            ? TransactionAnalyticsTrendDto.builder()
                .withPeriod(parameters.comparePeriod())
                .withData(compareItems)
                .build()
            : null;

    return TransactionAnalyticsTrendDto.builder()
        .withPeriod(parameters.period())
        .withData(currentItems)
        .withCompare(compareTrend)
        .build();
  }
}
