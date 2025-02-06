package it.moneyverse.budget.runtime.controllers;

import it.moneyverse.budget.model.dto.*;
import it.moneyverse.budget.services.BudgetService;
import it.moneyverse.budget.services.CategoryService;
import it.moneyverse.core.model.dto.PageCriteria;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "${spring.security.base-path}")
@Validated
public class BudgetManagementController implements CategoryOperations, BudgetOperations {

  private final CategoryService categoryService;
  private final BudgetService budgetService;

  public BudgetManagementController(CategoryService categoryService, BudgetService budgetService) {
    this.categoryService = categoryService;
    this.budgetService = budgetService;
  }

  @Override
  @PostMapping("/categories")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("@securityService.isAuthenticatedUserOwner(#request.userId())")
  public CategoryDto createCategory(@RequestBody CategoryRequestDto request) {
    return categoryService.createCategory(request);
  }

  @Override
  @GetMapping("/categories")
  @ResponseStatus(HttpStatus.OK)
  public List<CategoryDto> getCategories(
      @RequestParam(name = "default", required = false, defaultValue = "false")
          Boolean defaultCategories) {
    return categoryService.getCategories(defaultCategories);
  }

  @Override
  @GetMapping("/categories/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("(@securityService.isAuthenticatedUserOwner(#userId))")
  public List<CategoryDto> getCategoriesByUser(
      @PathVariable UUID userId, PageCriteria pageCriteria) {
    return categoryService.getCategoriesByUserId(userId, pageCriteria);
  }

  @Override
  @GetMapping("/categories/{categoryId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "@categoryRepository.existsByUserIdAndCategoryId(@securityService.getAuthenticatedUserId(), #categoryId)")
  public CategoryDto getCategory(@PathVariable UUID categoryId) {
    return categoryService.getCategory(categoryId);
  }

  @Override
  @PutMapping("/categories/{categoryId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "@categoryRepository.existsByUserIdAndCategoryId(@securityService.getAuthenticatedUserId(), #categoryId)")
  public CategoryDto updateCategory(
      @PathVariable UUID categoryId, @RequestBody CategoryUpdateRequestDto request) {
    return categoryService.updateCategory(categoryId, request);
  }

  @Override
  @DeleteMapping("/categories/{categoryId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "@categoryRepository.existsByUserIdAndCategoryId(@securityService.getAuthenticatedUserId(), #categoryId)")
  public void deleteCategory(@PathVariable UUID categoryId) {
    categoryService.deleteCategory(categoryId);
  }

  @Override
  @PostMapping("/budgets")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize(
      "@categoryRepository.existsByUserIdAndCategoryId(@securityService.getAuthenticatedUserId(), #request.categoryId())")
  public BudgetDto createBudget(@RequestBody BudgetRequestDto request) {
    return budgetService.createBudget(request);
  }

  @Override
  @GetMapping("/budgets/users/{userId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("(@securityService.isAuthenticatedUserOwner(#userId))")
  public List<BudgetDto> getBudgetsByUserId(@PathVariable UUID userId, BudgetCriteria criteria) {
    return budgetService.getBudgetsByUserId(userId, criteria);
  }

  @Override
  @GetMapping("/budgets/{budgetId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "@budgetRepository.existsByCategory_UserIdAndBudgetId(@securityService.getAuthenticatedUserId(), #budgetId)")
  public BudgetDto getBudget(@PathVariable UUID budgetId) {
    return budgetService.getBudget(budgetId);
  }

  @Override
  @PutMapping("/budgets/{budgetId}")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize(
      "@budgetRepository.existsByCategory_UserIdAndBudgetId(@securityService.getAuthenticatedUserId(), #budgetId)")
  public BudgetDto updateBudget(
      @PathVariable UUID budgetId, @RequestBody BudgetUpdateRequestDto request) {
    return budgetService.updateBudget(budgetId, request);
  }

  @Override
  @DeleteMapping("/budgets/{budgetId}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @PreAuthorize(
      "@budgetRepository.existsByCategory_UserIdAndBudgetId(@securityService.getAuthenticatedUserId(), #budgetId)")
  public void deleteBudget(@PathVariable UUID budgetId) {
    budgetService.deleteBudget(budgetId);
  }
}
