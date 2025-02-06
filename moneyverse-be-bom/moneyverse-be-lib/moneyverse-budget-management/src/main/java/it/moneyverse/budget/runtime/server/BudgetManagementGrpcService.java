package it.moneyverse.budget.runtime.server;

import io.grpc.stub.StreamObserver;
import it.moneyverse.budget.model.repositories.CategoryRepository;
import it.moneyverse.grpc.lib.BudgetServiceGrpc;
import it.moneyverse.grpc.lib.CategoryRequest;
import it.moneyverse.grpc.lib.CategoryResponse;
import java.util.UUID;

class BudgetManagementGrpcService extends BudgetServiceGrpc.BudgetServiceImplBase {

  private final CategoryRepository categoryRepository;

  public BudgetManagementGrpcService(CategoryRepository categoryRepository) {
    this.categoryRepository = categoryRepository;
  }

  @Override
  public void checkIfCategoryExists(
      CategoryRequest request, StreamObserver<CategoryResponse> responseObserver) {
    boolean exists =
        categoryRepository.existsByCategoryId(UUID.fromString(request.getCategoryId()));
    CategoryResponse response = CategoryResponse.newBuilder().setExists(exists).build();
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
