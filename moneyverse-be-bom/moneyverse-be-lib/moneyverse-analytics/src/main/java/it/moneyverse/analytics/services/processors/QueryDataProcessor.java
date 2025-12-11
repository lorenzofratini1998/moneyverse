package it.moneyverse.analytics.services.processors;

import it.moneyverse.analytics.enums.QueryPeriodTypeEnum;
import it.moneyverse.analytics.model.dto.FilterDto;
import java.util.List;
import java.util.function.Function;

public class QueryDataProcessor {

  public static <P> P getCurrentData(
      List<P> data, Function<P, QueryPeriodTypeEnum> periodExtractor) {
    return data.stream()
        .filter(d -> periodExtractor.apply(d) == QueryPeriodTypeEnum.CURRENT)
        .findFirst()
        .orElse(null);
  }

  public static <P> List<P> getCurrentDataList(
      List<P> data, Function<P, QueryPeriodTypeEnum> periodExtractor) {
    return data.stream()
        .filter(d -> periodExtractor.apply(d) == QueryPeriodTypeEnum.CURRENT)
        .toList();
  }

  public static <P> P getCompareData(
      List<P> data, FilterDto parameters, Function<P, QueryPeriodTypeEnum> periodExtractor) {
    if (parameters.comparePeriod() == null) {
      return null;
    }
    return data.stream()
        .filter(d -> periodExtractor.apply(d) == QueryPeriodTypeEnum.COMPARE)
        .findFirst()
        .orElse(null);
  }

  public static <P> List<P> getCompareDataList(
      List<P> data, FilterDto parameters, Function<P, QueryPeriodTypeEnum> periodExtractor) {
    if (parameters.comparePeriod() == null) {
      return null;
    }
    return data.stream()
        .filter(d -> periodExtractor.apply(d) == QueryPeriodTypeEnum.COMPARE)
        .toList();
  }

  private QueryDataProcessor() {}
}
