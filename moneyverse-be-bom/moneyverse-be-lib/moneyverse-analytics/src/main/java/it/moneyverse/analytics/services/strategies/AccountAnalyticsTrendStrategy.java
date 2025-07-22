package it.moneyverse.analytics.services.strategies;

import it.moneyverse.analytics.model.dto.AccountAnalyticsTrendDto;
import it.moneyverse.analytics.model.dto.AmountDto;
import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.dto.PeriodDto;
import it.moneyverse.analytics.model.projections.AccountAnalyticsTrendProjection;
import it.moneyverse.analytics.utils.AnalyticsUtils;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class AccountAnalyticsTrendStrategy
    implements AccountAnalyticsStrategy<
        List<AccountAnalyticsTrendDto>, List<AccountAnalyticsTrendProjection>> {
  @Override
  public List<AccountAnalyticsTrendDto> calculate(
      List<AccountAnalyticsTrendProjection> currentData,
      List<AccountAnalyticsTrendProjection> compareData,
      FilterDto parameters) {
    Map<UUID, List<AccountAnalyticsTrendProjection>> currentDataMap = groupByAccountId(currentData);
    Map<UUID, List<AccountAnalyticsTrendProjection>> compareDataMap = groupByAccountId(compareData);
    List<AccountAnalyticsTrendDto> result = new ArrayList<>();

    for (UUID accountId : currentDataMap.keySet()) {
      List<AmountDto> compareAmounts = null;
      if (parameters.comparePeriod() != null && compareDataMap.containsKey(accountId)) {
        compareAmounts =
            compareDataMap.get(accountId).stream()
                .map(
                    p ->
                        AmountDto.builder()
                            .withPeriod(new PeriodDto(p.startDate(), p.endDate()))
                            .withAmount(p.totalAmount())
                            .build())
                .toList();
      }
      AccountAnalyticsTrendDto compareDto =
          compareAmounts != null
              ? AccountAnalyticsTrendDto.builder()
                  .withPeriod(parameters.comparePeriod())
                  .withAccountId(accountId)
                  .withData(compareAmounts)
                  .build()
              : null;

      Map<PeriodDto, BigDecimal> compareMap =
          compareDataMap.getOrDefault(accountId, List.of()).stream()
              .collect(
                  Collectors.toMap(
                      p -> new PeriodDto(p.startDate(), p.endDate()),
                      AccountAnalyticsTrendProjection::totalAmount));

      List<AmountDto> currentAmounts =
          currentDataMap.get(accountId).stream()
              .map(
                  p -> {
                    PeriodDto period = new PeriodDto(p.startDate(), p.endDate());
                    BigDecimal totalAmount = p.totalAmount();
                    return AmountDto.builder()
                        .withPeriod(period)
                        .withAmount(totalAmount)
                        .withVariation(
                            AnalyticsUtils.calculateTrend(p.totalAmount(), compareMap.get(period)))
                        .build();
                  })
              .toList();

      AccountAnalyticsTrendDto currentDto =
          AccountAnalyticsTrendDto.builder()
              .withPeriod(parameters.period())
              .withAccountId(accountId)
              .withData(currentAmounts)
              .withCompare(compareDto)
              .build();
      result.add(currentDto);
    }
    return result;
  }

  private Map<UUID, List<AccountAnalyticsTrendProjection>> groupByAccountId(
      List<AccountAnalyticsTrendProjection> data) {
    if (data == null) return Collections.emptyMap();
    return data.stream().collect(Collectors.groupingBy(AccountAnalyticsTrendProjection::accountId));
  }
}
