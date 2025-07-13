package it.moneyverse.account.runtime.server;

import io.grpc.stub.StreamObserver;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.grpc.lib.AccountRequest;
import it.moneyverse.grpc.lib.AccountResponse;
import it.moneyverse.grpc.lib.AccountServiceGrpc;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

class AccountManagementGrpcService extends AccountServiceGrpc.AccountServiceImplBase {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountManagementGrpcService.class);
  private final AccountRepository accountRepository;
  private final TransactionTemplate transactionTemplate;

  AccountManagementGrpcService(
      AccountRepository accountRepository, PlatformTransactionManager platformTransactionManager) {
    this.accountRepository = accountRepository;
    this.transactionTemplate = new TransactionTemplate(platformTransactionManager);
    this.transactionTemplate.setReadOnly(true);
  }

  @Transactional(readOnly = true)
  @Override
  public void getAccountById(
      AccountRequest request, StreamObserver<AccountResponse> responseObserver) {
    AccountResponse response =
        transactionTemplate.execute(
            status ->
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
                                .setAccountDescription(
                                    Optional.ofNullable(account.getAccountDescription()).orElse(""))
                                .setCurrency(account.getCurrency())
                                .setIsDefault(account.isDefault())
                                .build())
                    .orElseGet(
                        () -> {
                          LOGGER.info("Account {} not found", request.getAccountId());
                          return AccountResponse.getDefaultInstance();
                        }));
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}
