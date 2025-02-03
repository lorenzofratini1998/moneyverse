package it.moneyverse.budget.runtime.controllers;

import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.dto.BudgetUpdateRequestDto;
import it.moneyverse.budget.services.BudgetService;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "${spring.security.base-path}")
@Validated
public class BudgetManagementController implements BudgetOperations {

  private final BudgetService budgetService;

  public BudgetManagementController(BudgetService budgetService) {
    this.budgetService = budgetService;
  }

  @Override
  @PostMapping("/budgets")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("@securityService.isAuthenticatedUserOwner(#request.userId())")
  public BudgetDto createBudget(@RequestBody BudgetRequestDto request) {
    return budgetService.createBudget(request);
  }

  @Override
  @GetMapping("/budgets/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("(@securityService.isAuthenticatedUserOwner(#userId))")
  public List<BudgetDto> getBudgets(@PathVariable UUID userId, BudgetCriteria criteria) {
    return budgetService.getBudgets(userId, criteria);
  }

  @Override
  @GetMapping("/budgets/{budgetId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "@budgetRepository.existsByUserIdAndBudgetId(@securityService.getAuthenticatedUserId(), #budgetId)")
  public BudgetDto getBudget(@PathVariable UUID budgetId) {
    return budgetService.getBudget(budgetId);
  }

  @Override
  @PutMapping("/budgets/{budgetId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "@budgetRepository.existsByUserIdAndBudgetId(@securityService.getAuthenticatedUserId(), #budgetId)")
  public BudgetDto updateBudget(
      @PathVariable UUID budgetId, @RequestBody BudgetUpdateRequestDto request) {
    return budgetService.updateBudget(budgetId, request);
  }

  @Override
  @DeleteMapping("/budgets/{budgetId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "@budgetRepository.existsByUserIdAndBudgetId(@securityService.getAuthenticatedUserId(), #budgetId)")
  public void deleteBudget(@PathVariable UUID budgetId) {
    budgetService.deleteBudget(budgetId);
  }
}
