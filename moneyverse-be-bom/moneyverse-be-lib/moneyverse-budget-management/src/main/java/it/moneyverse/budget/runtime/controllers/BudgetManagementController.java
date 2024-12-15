package it.moneyverse.budget.runtime.controllers;

import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.services.BudgetService;
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
  @PreAuthorize(
      "hasRole(T(it.moneyverse.core.enums.UserRoleEnum).ADMIN.name()) or #request.username == authentication.name")
  public BudgetDto createBudget(@RequestBody BudgetRequestDto request) {
    return budgetService.createBudget(request);
  }
}
