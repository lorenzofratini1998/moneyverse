package it.moneyverse.analytics.model.queries;

import java.util.Map;
import org.springframework.jdbc.core.RowMapper;

public interface Query<T, P> {
  String getSql();

  Map<String, ?> getParameters(P parameters);

  RowMapper<T> getRowMapper();
}
