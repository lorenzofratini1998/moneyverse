package it.moneyverse.transaction.utils.mapper;

import it.moneyverse.test.annotations.datasource.TestModelEntity;
import it.moneyverse.test.model.TestContext;
import it.moneyverse.test.model.dto.ScriptMetadata;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.services.SQLScriptService;
import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.entities.TagFactory;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.entities.TransactionFactory;
import java.nio.file.Path;
import java.util.List;

public class TransactionTestContext extends TestContext<TransactionTestContext> {

  private static TransactionTestContext currentInstance;

  @TestModelEntity private final List<Tag> tags;
  @TestModelEntity private final List<Transaction> transactions;

  public TransactionTestContext() {
    super();
    this.tags = TagFactory.createTags(getUsers());
    this.transactions = TransactionFactory.createTransactions(getUsers(), tags);
  }

  private static void setCurrentInstance(TransactionTestContext testContext) {
    currentInstance = testContext;
  }

  private static TransactionTestContext getCurrentInstance() {
    if (currentInstance == null) {
      throw new IllegalStateException("TransactionTestContext has not been initialized");
    }
    return currentInstance;
  }

  public List<Tag> getTags() {
    return tags;
  }

  public List<Transaction> getTransactions() {
    return transactions;
  }

  public TransactionRequestDto createTransactionRequest(String username) {
    Transaction transaction = TransactionFactory.fakeTransaction(username);
    return new TransactionRequestDto(
        transaction.getUsername(),
        transaction.getAccountId(),
        transaction.getBudgetId(),
        transaction.getDate(),
        transaction.getDescription(),
        transaction.getAmount(),
        transaction.getCurrency(),
        null);
  }

  public TransactionDto getExpectedTransactionDto(TransactionRequestDto request) {
    return TransactionDto.builder()
        .withUsername(request.username())
        .withAccountId(request.accountId())
        .withBudgetId(request.budgetId())
        .withDescription(request.description())
        .withAmount(request.amount())
        .withCurrency(request.currency())
        .withDate(request.date())
        .build();
  }

  @Override
  public TransactionTestContext self() {
    return this;
  }

  @Override
  public TransactionTestContext generateScript(Path dir) {
    new EntityScriptGenerator(new ScriptMetadata(dir, tags, transactions), new SQLScriptService())
        .execute();
    return self();
  }
}
