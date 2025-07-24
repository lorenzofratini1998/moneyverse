package it.moneyverse.analytics.runtime.controllers;

import it.moneyverse.analytics.model.dto.*;
import it.moneyverse.analytics.services.AccountAnalyticsService;
import it.moneyverse.analytics.services.CategoryAnalyticsService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "${spring.security.base-path}")
@Validated
public class AnalyticsController
    implements AccountAnalyticsOperations, CategoryAnalyticsOperations {

  private final AccountAnalyticsService accountAnalyticsService;
  private final CategoryAnalyticsService categoryAnalyticsService;

  public AnalyticsController(
      AccountAnalyticsService accountAnalyticsService,
      CategoryAnalyticsService categoryAnalyticsService) {
    this.accountAnalyticsService = accountAnalyticsService;
    this.categoryAnalyticsService = categoryAnalyticsService;
  }

  @Override
  @PostMapping("/accounts/kpi")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.isAuthenticatedUserOwner(#filter.userId)")
  public AccountAnalyticsKpiDto calculateAccountKpi(@RequestBody FilterDto filter) {
    return accountAnalyticsService.calculateKpi(filter);
  }

  @Override
  @PostMapping("/accounts/distribution")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.isAuthenticatedUserOwner(#filter.userId)")
  public List<AccountAnalyticsDistributionDto> calculateAccountDistribution(
      @RequestBody FilterDto filter) {
    return accountAnalyticsService.calculateDistribution(filter);
  }

  @Override
  @PostMapping("/accounts/trend")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.isAuthenticatedUserOwner(#filter.userId)")
  public List<AccountAnalyticsTrendDto> calculateAccountTrend(@RequestBody FilterDto filter) {
    return accountAnalyticsService.calculateTrend(filter);
  }

  @Override
  @PostMapping("/categories/kpi")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.isAuthenticatedUserOwner(#filter.userId)")
  public CategoryAnalyticsKpiDto calculateCategoryKpi(@RequestBody FilterDto filter) {
    return categoryAnalyticsService.calculateKpi(filter);
  }

  @Override
  @PostMapping("/categories/distribution")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.isAuthenticatedUserOwner(#filter.userId)")
  public List<CategoryAnalyticsDistributionDto> calculateCategoryDistribution(
      @RequestBody FilterDto filter) {
    return categoryAnalyticsService.calculateDistribution(filter);
  }

  @Override
  @PostMapping("/categories/trend")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.isAuthenticatedUserOwner(#filter.userId)")
  public List<CategoryAnalyticsTrendDto> calculateCategoryTrend(@RequestBody FilterDto filter) {
    return categoryAnalyticsService.calculateTrend(filter);
  }
}
