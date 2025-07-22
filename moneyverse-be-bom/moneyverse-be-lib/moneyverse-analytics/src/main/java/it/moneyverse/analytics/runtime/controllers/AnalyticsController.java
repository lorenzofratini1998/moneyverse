package it.moneyverse.analytics.runtime.controllers;

import it.moneyverse.analytics.model.dto.AccountAnalyticsDistributionDto;
import it.moneyverse.analytics.model.dto.AccountAnalyticsKpiDto;
import it.moneyverse.analytics.model.dto.AccountAnalyticsTrendDto;
import it.moneyverse.analytics.model.dto.FilterDto;
import it.moneyverse.analytics.services.AccountAnalyticsService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "${spring.security.base-path}")
@Validated
public class AnalyticsController implements AccountAnalyticsOperations {

  private final AccountAnalyticsService accountAnalyticsService;

  public AnalyticsController(AccountAnalyticsService accountAnalyticsService) {
    this.accountAnalyticsService = accountAnalyticsService;
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
}
