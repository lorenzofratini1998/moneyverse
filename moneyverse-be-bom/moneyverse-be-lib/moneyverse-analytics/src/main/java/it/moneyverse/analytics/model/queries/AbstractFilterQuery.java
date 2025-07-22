package it.moneyverse.analytics.model.queries;

import it.moneyverse.analytics.model.dto.FilterDto;
import java.util.*;
import org.springframework.jdbc.core.RowMapper;

public abstract class AbstractFilterQuery<T> implements Query<T, FilterDto> {

  @Override
  public Map<String, Object> getParameters(FilterDto parameters) {
    List<UUID> accounts =
        Optional.ofNullable(parameters.accounts()).orElse(Collections.emptyList());
    List<UUID> categories =
        Optional.ofNullable(parameters.categories()).orElse(Collections.emptyList());
    List<UUID> tags = Optional.ofNullable(parameters.tags()).orElse(Collections.emptyList());
    String currency = Optional.ofNullable(parameters.currency()).orElse("");
    boolean hasComparePeriod = parameters.comparePeriod() != null;

    Map<String, Object> params = new HashMap<>();
    params.put("userId", parameters.userId());
    params.put("startDate", parameters.period().startDate());
    params.put("endDate", parameters.period().endDate());
    params.put("accounts", accounts);
    params.put("categories", categories);
    params.put("tags", tags);
    params.put("currency", currency);
    params.put("hasComparePeriod", hasComparePeriod);
    params.put(
        "compareStartDate", hasComparePeriod ? parameters.comparePeriod().startDate() : null);
    params.put("compareEndDate", hasComparePeriod ? parameters.comparePeriod().endDate() : null);
    return params;
  }

  @Override
  public abstract String getSql();

  @Override
  public abstract RowMapper<T> getRowMapper();
}
