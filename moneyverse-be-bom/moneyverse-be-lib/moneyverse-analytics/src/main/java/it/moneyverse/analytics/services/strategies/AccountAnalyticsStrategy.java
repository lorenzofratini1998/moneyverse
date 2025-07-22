package it.moneyverse.analytics.services.strategies;

import it.moneyverse.analytics.model.dto.FilterDto;

public interface AccountAnalyticsStrategy<T, P> {
  T calculate(P currentData, P compareData, FilterDto parameters);
}
