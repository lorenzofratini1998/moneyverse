package it.moneyverse.analytics.services.strategies;

import it.moneyverse.analytics.model.dto.AmountDto;
import it.moneyverse.analytics.model.dto.CategoryAnalyticsTrendDto;
import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.dto.PeriodDto;
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
    Map<UUID, List<CategoryAnalyticsTrendProjection>> currentDataMap =
        groupByCategoryId(currentData);
    Map<UUID, List<CategoryAnalyticsTrendProjection>> compareDataMap =
        groupByCategoryId(compareData);
    List<CategoryAnalyticsTrendDto> result = new ArrayList<>();

    for (UUID categoryId : currentDataMap.keySet()) {
      List<AmountDto> compareAmounts = null;
      if (parameters.comparePeriod() != null && compareDataMap.containsKey(categoryId)) {
        compareAmounts =
            compareDataMap.get(categoryId).stream()
                .map(
                    p ->
                        AmountDto.builder()
                            .withPeriod(new PeriodDto(p.startDate(), p.endDate()))
                            .withAmount(p.totalAmount())
                            .build())
                .toList();
      }
      CategoryAnalyticsTrendDto compareDto =
          compareAmounts != null
              ? CategoryAnalyticsTrendDto.builder()
                  .withPeriod(parameters.comparePeriod())
                  .withCategoryId(categoryId)
                  .withData(compareAmounts)
                  .build()
              : null;

      Map<PeriodDto, BigDecimal> compareMap =
          compareDataMap.getOrDefault(categoryId, Collections.emptyList()).stream()
              .collect(
                  Collectors.toMap(
                      p -> new PeriodDto(p.startDate(), p.endDate()),
                      CategoryAnalyticsTrendProjection::totalAmount));

      List<AmountDto> currentAmounts =
          currentDataMap.get(categoryId).stream()
              .map(
                  p -> {
                    PeriodDto period = new PeriodDto(p.startDate(), p.endDate());
                    BigDecimal amount = p.totalAmount();
                    return AmountDto.builder()
                        .withPeriod(period)
                        .withAmount(amount)
                        .withVariation(
                            AnalyticsUtils.calculateTrend(
                                amount, compareMap.getOrDefault(period, null)))
                        .build();
                  })
              .toList();

      CategoryAnalyticsTrendDto currentDto =
          CategoryAnalyticsTrendDto.builder()
              .withPeriod(parameters.period())
              .withCategoryId(categoryId)
              .withData(currentAmounts)
              .withCompare(compareDto)
              .build();
      result.add(currentDto);
    }
    return result;
  }

  private Map<UUID, List<CategoryAnalyticsTrendProjection>> groupByCategoryId(
      List<CategoryAnalyticsTrendProjection> data) {
    if (data == null) return Collections.emptyMap();
    return data.stream()
        .collect(Collectors.groupingBy(CategoryAnalyticsTrendProjection::categoryId));
  }
}
