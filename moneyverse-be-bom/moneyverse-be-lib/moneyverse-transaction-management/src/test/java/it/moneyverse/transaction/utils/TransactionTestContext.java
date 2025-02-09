package it.moneyverse.transaction.utils;

import static it.moneyverse.transaction.enums.TransactionSortAttributeEnum.*;

import it.moneyverse.core.enums.SortAttribute;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.test.annotations.datasource.TestModelEntity;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.model.TestContext;
import it.moneyverse.test.model.dto.ScriptMetadata;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.services.SQLScriptService;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.enums.TransactionSortAttributeEnum;
import it.moneyverse.transaction.model.dto.TransactionCriteria;
import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import it.moneyverse.transaction.model.dto.TransactionRequestItemDto;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.entities.TagFactory;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.entities.TransactionFactory;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.domain.Sort;

public class TransactionTestContext extends TestContext<TransactionTestContext> {

  private static TransactionTestContext currentInstance;

  @TestModelEntity private final List<Tag> tags;
  @TestModelEntity private final List<Transaction> transactions;

  public TransactionTestContext(KeycloakContainer keycloakContainer) {
    super(keycloakContainer);
    this.tags = TagFactory.createTags(getUsers());
    this.transactions = TransactionFactory.createTransactions(getUsers(), tags);
    setCurrentInstance(this);
  }

  public TransactionTestContext() {
    super();
    this.tags = TagFactory.createTags(getUsers());
    this.transactions = TransactionFactory.createTransactions(getUsers(), tags);
    setCurrentInstance(this);
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

  public List<Transaction> getTransactions(UUID userId) {
    return transactions.stream().filter(t -> t.getUserId().equals(userId)).toList();
  }

  public List<Transaction> getTransactionsByAccountId(UUID accountId) {
    return transactions.stream().filter(t -> t.getAccountId().equals(accountId)).toList();
  }

  public List<Transaction> getTransactionsByBudgetId(UUID budgetId) {
    return transactions.stream().filter(t -> t.getAccountId().equals(budgetId)).toList();
  }

  public TransactionRequestDto createTransactionRequest(UUID userId) {
    Transaction transaction = TransactionFactory.fakeTransaction(userId);
    return new TransactionRequestDto(
        transaction.getUserId(),
        List.of(
            new TransactionRequestItemDto(
                transaction.getAccountId(),
                transaction.getBudgetId(),
                transaction.getDate(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getCurrency(),
                null)));
  }

  public TransactionDto getExpectedTransactionDto(TransactionRequestDto request) {
    TransactionRequestItemDto item = request.transactions().getFirst();
    return TransactionDto.builder()
        .withUserId(request.userId())
        .withAccountId(item.accountId())
        .withBudgetId(item.categoryId())
        .withDescription(item.description())
        .withAmount(item.amount())
        .withCurrency(item.currency())
        .withDate(item.date())
        .build();
  }

  public List<Transaction> filterTransactions(UUID userId, TransactionCriteria criteria) {
    return transactions.stream()
        .filter(transaction -> transaction.getUserId().equals(userId))
        .filter(
            transaction ->
                criteria.getAccounts().isEmpty()
                    || criteria.getAccounts().get().contains(transaction.getAccountId()))
        .filter(
            transaction ->
                criteria.getBudgets().isEmpty()
                    || criteria.getBudgets().get().contains(transaction.getBudgetId()))
        .filter(
            transaction ->
                criteria.getDate().map(date -> date.matches(transaction.getDate())).orElse(true))
        .filter(
            transaction ->
                criteria
                    .getAmount()
                    .map(amount -> amount.matches(transaction.getAmount()))
                    .orElse(true))
        .filter(
            transaction ->
                criteria.getTags().isEmpty()
                    || transaction.getTags().stream().anyMatch(criteria.getTags().get()::contains))
        .sorted((a, b) -> sortByCriteria(a, b, criteria.getSort()))
        .skip(criteria.getPage().getOffset())
        .limit(criteria.getPage().getLimit())
        .toList();
  }

  private int sortByCriteria(
      Transaction a, Transaction b, SortCriteria<TransactionSortAttributeEnum> sortCriteria) {
    if (sortCriteria == null) {
      return 0;
    }

    SortAttribute attribute = sortCriteria.getAttribute();
    Sort.Direction direction = sortCriteria.getDirection();

    int comparison =
        switch (attribute) {
          case DATE -> a.getDate().compareTo(b.getDate());
          case AMOUNT -> a.getAmount().compareTo(b.getAmount());
          default -> 0;
        };

    return direction == Sort.Direction.ASC ? comparison : -comparison;
  }

  public TransactionCriteria createTransactionCriteria(UUID userId) {
    return new TransactionCriteriaRandomGenerator(userId, getCurrentInstance()).generate();
  }

  public Transaction getRandomTransaction(UUID userId) {
    List<Transaction> userTransactions =
        transactions.stream().filter(t -> t.getUserId().equals(userId)).toList();
    return userTransactions.get(RandomUtils.randomInteger(0, userTransactions.size() - 1));
  }

  public Set<UUID> getRandomTag(UUID userId) {
    List<Tag> userTags = tags.stream().filter(t -> t.getUserId().equals(userId)).toList();
    return userTags.isEmpty()
        ? null
        : Collections.singleton(
            userTags.get(RandomUtils.randomInteger(0, userTags.size() - 1)).getTagId());
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
