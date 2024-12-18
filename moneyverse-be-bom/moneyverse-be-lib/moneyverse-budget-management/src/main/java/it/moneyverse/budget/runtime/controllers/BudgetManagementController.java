package it.moneyverse.budget.runtime.controllers;

import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.dto.BudgetUpdateRequestDto;
import it.moneyverse.budget.services.BudgetService;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

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

  @Override
  @GetMapping("/budgets")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "hasRole(T(it.moneyverse.core.enums.UserRoleEnum).ADMIN.name()) or (#criteria.username.isPresent() and #criteria.username.get().equals(authentication.name))")
  public List<BudgetDto> getBudgets(BudgetCriteria criteria) {
    return budgetService.getBudgets(criteria);
  }

  @Override
  @GetMapping("/budgets/{budgetId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
          "hasRole(T(it.moneyverse.core.enums.UserRoleEnum).ADMIN.name()) or (@budgetRepository.existsByUsernameAndBudgetId(authentication.name, #budgetId))")
  public BudgetDto getBudget(@PathVariable UUID budgetId) {
    return budgetService.getBudget(budgetId);
  }

  @Override
  @PutMapping("/budgets/{budgetId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
          "hasRole(T(it.moneyverse.core.enums.UserRoleEnum).ADMIN.name()) or (@budgetRepository.existsByUsernameAndBudgetId(authentication.name, #budgetId))"
  )
  public BudgetDto updateBudget(@PathVariable UUID budgetId, @RequestBody BudgetUpdateRequestDto request) {
    return budgetService.updateBudget(budgetId, request);
  }
}
