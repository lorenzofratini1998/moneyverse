package it.moneyverse.transaction.runtime.controllers;

import it.moneyverse.transaction.model.dto.CategoryDashboardDto;
import it.moneyverse.transaction.model.dto.DashboardFilterRequestDto;
import it.moneyverse.transaction.services.DashboardService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/dashboard/api/v1")
@Validated
public class DashboardController implements DashboardOperations {

  private final DashboardService dashboardService;

  public DashboardController(DashboardService dashboardService) {
    this.dashboardService = dashboardService;
  }

  @Override
  @PostMapping("/categories")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.isAuthenticatedUserOwner(#request.userId())")
  public CategoryDashboardDto calculateCategoryDashboard(
      @RequestBody DashboardFilterRequestDto request) {
    return dashboardService.calculateCategoryDashboard(request);
  }
}
