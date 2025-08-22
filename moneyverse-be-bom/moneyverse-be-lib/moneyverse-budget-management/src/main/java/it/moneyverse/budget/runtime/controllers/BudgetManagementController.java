package it.moneyverse.budget.runtime.controllers;

import it.moneyverse.budget.model.dto.*;
import it.moneyverse.budget.services.BudgetService;
import it.moneyverse.budget.services.CategoryService;
import it.moneyverse.core.model.dto.BudgetDto;
import it.moneyverse.core.model.dto.CategoryDto;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.events.SseEmitterRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping(value = "${spring.security.base-path}")
@Validated
public class BudgetManagementController implements CategoryOperations, BudgetOperations {

  private final CategoryService categoryService;
  private final BudgetService budgetService;
  private final SseEmitterRepository sseEmitterRepository;

  public BudgetManagementController(
      CategoryService categoryService,
      BudgetService budgetService,
      SseEmitterRepository sseEmitterRepository) {
    this.categoryService = categoryService;
    this.budgetService = budgetService;
    this.sseEmitterRepository = sseEmitterRepository;
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
  @PostMapping("/categories/users/{userId}/default")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("(@securityService.isAuthenticatedUserOwner(#userId))")
  public void createUserDefaultCategories(@PathVariable UUID userId) {
    categoryService.createUserDefaultCategories(userId);
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
  @GetMapping("/categories/users/{userId}/tree")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("@securityService.isAuthenticatedUserOwner(#userId)")
  public List<CategoryDto> getCategoryTreeByUserId(@PathVariable UUID userId) {
    return categoryService.getCategoryTreeByUserId(userId);
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

  @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public SseEmitter subscribe(@RequestParam UUID userId) {
    SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
    sseEmitterRepository.add(userId, emitter);

    emitter.onCompletion(() -> sseEmitterRepository.remove(userId, emitter));
    emitter.onTimeout(() -> sseEmitterRepository.remove(userId, emitter));
    emitter.onError((error) -> sseEmitterRepository.remove(userId, emitter));

    return emitter;
  }
}
