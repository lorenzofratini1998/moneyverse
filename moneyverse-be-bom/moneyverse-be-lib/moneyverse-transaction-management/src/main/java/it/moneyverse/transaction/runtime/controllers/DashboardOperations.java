package it.moneyverse.transaction.runtime.controllers;

import it.moneyverse.transaction.model.dto.CategoryDashboardDto;
import it.moneyverse.transaction.model.dto.DashboardFilterRequestDto;
import jakarta.validation.Valid;

public interface DashboardOperations {
  CategoryDashboardDto calculateCategoryDashboard(@Valid DashboardFilterRequestDto request);
}
