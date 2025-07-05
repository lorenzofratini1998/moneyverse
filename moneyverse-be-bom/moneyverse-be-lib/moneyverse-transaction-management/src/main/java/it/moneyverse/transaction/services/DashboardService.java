package it.moneyverse.transaction.services;

import it.moneyverse.transaction.model.dto.CategoryDashboardDto;
import it.moneyverse.transaction.model.dto.DashboardFilterRequestDto;

public interface DashboardService {
  CategoryDashboardDto calculateCategoryDashboard(DashboardFilterRequestDto request);
}
