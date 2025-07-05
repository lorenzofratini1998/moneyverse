package it.moneyverse.transaction.model.dto.projection;

import java.math.BigDecimal;
import java.util.UUID;

public interface CategoryMonthlyStatsProjection {
  UUID getCategoryId();

  Integer getYear();

  Integer getMonth();

  BigDecimal getTotal();
}
