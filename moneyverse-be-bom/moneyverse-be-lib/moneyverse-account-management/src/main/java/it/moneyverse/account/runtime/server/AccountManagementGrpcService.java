package it.moneyverse.account.runtime.server;

import io.grpc.stub.StreamObserver;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.grpc.lib.AccountRequest;
import it.moneyverse.grpc.lib.AccountResponse;
import it.moneyverse.grpc.lib.AccountServiceGrpc;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class AccountManagementGrpcService extends AccountServiceGrpc.AccountServiceImplBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountManagementGrpcService.class);
  private final AccountRepository accountRepository;

  AccountManagementGrpcService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  public void getAccountById(
      AccountRequest request, StreamObserver<AccountResponse> responseObserver) {
    AccountResponse response =
        accountRepository
            .findById(UUID.fromString(request.getAccountId()))
            .map(
                account ->
                    AccountResponse.newBuilder()
                        .setAccountId(account.getAccountId().toString())
                        .setUserId(account.getUserId().toString())
                        .setAccountName(account.getAccountName())
                        .setBalance(account.getBalance().doubleValue())
                        .setBalanceTarget(account.getBalanceTarget().doubleValue())
                        .setAccountCategory(account.getAccountCategory().getName())
                        .setAccountDescription(account.getAccountDescription())
                        .setCurrency(account.getCurrency())
                        .setIsDefault(account.isDefault())
                        .build())
            .orElseGet(
                () -> {
                  LOGGER.info("Account {} not found", request.getAccountId());
                  return AccountResponse.getDefaultInstance();
                });
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
