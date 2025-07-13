package it.moneyverse.budget.runtime.server;

import io.grpc.stub.StreamObserver;
import it.moneyverse.budget.model.repositories.BudgetRepository;
import it.moneyverse.budget.model.repositories.CategoryRepository;
import it.moneyverse.grpc.lib.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BudgetManagementGrpcService extends BudgetServiceGrpc.BudgetServiceImplBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(BudgetManagementGrpcService.class);
  private final CategoryRepository categoryRepository;
  private final BudgetRepository budgetRepository;

  public BudgetManagementGrpcService(
      CategoryRepository categoryRepository, BudgetRepository budgetRepository) {
    this.categoryRepository = categoryRepository;
    this.budgetRepository = budgetRepository;
  }

  @Override
  public void getCategoryById(
      CategoryRequest request, StreamObserver<CategoryResponse> responseObserver) {
    CategoryResponse response =
        categoryRepository
            .findById(UUID.fromString(request.getCategoryId()))
            .map(
                category ->
                    CategoryResponse.newBuilder()
                        .setCategoryId(category.getCategoryId().toString())
                        .setCategoryName(category.getCategoryName())
                        .setUserId(category.getUserId().toString())
                        .setDescription(Optional.ofNullable(category.getDescription()).orElse(""))
                        .build())
            .orElseGet(
                () -> {
                  LOGGER.info("Category {} not found", request.getCategoryId());
                  return CategoryResponse.getDefaultInstance();
                });
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getBudget(BudgetRequest request, StreamObserver<BudgetResponse> responseObserver) {
    BudgetResponse response =
        budgetRepository
            .findBudgetByCategoryAndDate(
                UUID.fromString(request.getCategoryId()), LocalDate.parse(request.getDate()))
            .map(
                budget ->
                    BudgetResponse.newBuilder()
                        .setBudgetId(budget.getBudgetId().toString())
                        .setCategoryId(budget.getCategory().getCategoryId().toString())
                        .setStartDate(budget.getStartDate().toString())
                        .setEndDate(budget.getEndDate().toString())
                        .build())
            .orElseGet(
                () -> {
                  LOGGER.info(
                      "Budget with category {} and data {} not found",
                      request.getCategoryId(),
                      request.getDate());
                  return BudgetResponse.getDefaultInstance();
                });
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
