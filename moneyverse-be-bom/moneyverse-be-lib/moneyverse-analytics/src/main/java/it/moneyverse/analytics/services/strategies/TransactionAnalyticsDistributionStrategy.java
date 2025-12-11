package it.moneyverse.analytics.services.strategies;

import it.moneyverse.analytics.model.dto.CountDto;
import it.moneyverse.analytics.model.dto.DistributionRangeDto;
import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.model.dto.TransactionAnalyticsDistributionDto;
import it.moneyverse.analytics.model.projections.TransactionAnalyticsDistributionProjection;
import it.moneyverse.analytics.utils.AnalyticsUtils;
import it.moneyverse.core.model.dto.BoundCriteria;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class TransactionAnalyticsDistributionStrategy
    implements AnalyticsStrategy<
        TransactionAnalyticsDistributionDto, List<TransactionAnalyticsDistributionProjection>> {

  @Override
  public TransactionAnalyticsDistributionDto calculate(
      List<TransactionAnalyticsDistributionProjection> currentData,
      List<TransactionAnalyticsDistributionProjection> compareData,
      FilterDto parameters) {
    TransactionAnalyticsDistributionDto compare =
        parameters.comparePeriod() != null
            ? TransactionAnalyticsDistributionDto.builder()
                .withPeriod(parameters.comparePeriod())
                .withData(getData(compareData))
                .build()
            : null;
    return TransactionAnalyticsDistributionDto.builder()
        .withPeriod(parameters.period())
        .withData(getData(currentData, compareData))
        .withCompare(compare)
        .build();
  }

  private List<DistributionRangeDto> getData(
      List<TransactionAnalyticsDistributionProjection> current) {
    return getData(current, null);
  }

  private List<DistributionRangeDto> getData(
      List<TransactionAnalyticsDistributionProjection> current,
      List<TransactionAnalyticsDistributionProjection> compare) {
    if (current == null) return null;

    Map<String, TransactionAnalyticsDistributionProjection> compareMap =
        compare != null
            ? compare.stream()
                .collect(
                    Collectors.toMap(
                        projection ->
                            createRangeKey(projection.lowerBound(), projection.upperBound()),
                        Function.identity()))
            : Collections.emptyMap();

    return current.stream()
        .map(
            d -> {
              TransactionAnalyticsDistributionProjection compareItem = compareMap.get(d.range());

              CountDto count =
                  AnalyticsUtils.getCount(
                      d,
                      compareItem,
                      TransactionAnalyticsDistributionProjection::numberOfTransactions);

              BoundCriteria boundCriteria = new BoundCriteria();
              boundCriteria.setLower(d.lowerBound());
              boundCriteria.setUpper(d.upperBound());

              return DistributionRangeDto.builder()
                  .withRange(boundCriteria)
                  .withCount(count)
                  .build();
            })
        .toList();
  }

  private String createRangeKey(BigDecimal lower, BigDecimal upper) {
    String lowerStr = lower != null ? lower.toString() : "null";
    String upperStr = upper != null ? upper.toString() : "null";
    return lowerStr + "-" + upperStr;
  }
}
