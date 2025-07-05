package it.moneyverse.transaction.model.dto.projection;

import java.math.BigDecimal;

public interface CategoryTotalsAndCountsProjection {
  BigDecimal getCurrentTotal();

  Integer getCurrentActiveCategoryCount();

  BigDecimal getPreviousTotal();

  Integer getPreviousActiveCategoryCount();
}
