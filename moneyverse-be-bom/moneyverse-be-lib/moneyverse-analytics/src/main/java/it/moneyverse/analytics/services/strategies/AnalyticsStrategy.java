package it.moneyverse.analytics.services.strategies;

import it.moneyverse.analytics.model.dto.FilterDto;

public interface AnalyticsStrategy<T, P> {
  T calculate(P currentData, P compareData, FilterDto parameters);
}
