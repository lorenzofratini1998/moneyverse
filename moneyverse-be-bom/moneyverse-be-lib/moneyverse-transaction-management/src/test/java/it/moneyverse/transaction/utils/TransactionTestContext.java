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
import it.moneyverse.transaction.model.dto.*;
import it.moneyverse.transaction.model.entities.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import org.springframework.data.domain.Sort;

public class TransactionTestContext extends TestContext<TransactionTestContext> {

  private static TransactionTestContext currentInstance;

  @TestModelEntity private final List<Tag> tags;
  @TestModelEntity private final List<Transaction> transactions;
  @TestModelEntity private final List<Transfer> transfers;
  @TestModelEntity private final List<Subscription> subscriptions;

  public TransactionTestContext(KeycloakContainer keycloakContainer) {
    super(keycloakContainer);
    this.tags = TagFactory.createTags(getUsers());
    this.transactions = TransactionFactory.createTransactions(getUsers(), tags);
    this.transfers = TransferFactory.createTransfers(getUsers(), transactions);
    this.subscriptions = SubscriptionFactory.createSubscriptions(getUsers(), transactions);
    setCurrentInstance(this);
  }

  public TransactionTestContext() {
    super();
    this.tags = TagFactory.createTags(getUsers());
    this.transactions = TransactionFactory.createTransactions(getUsers(), tags);
    this.transfers = TransferFactory.createTransfers(getUsers(), transactions);
    this.subscriptions = SubscriptionFactory.createSubscriptions(getUsers(), transactions);
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

  public List<Tag> getUserTags(UUID userId) {
    return tags.stream().filter(tag -> tag.getUserId().equals(userId)).toList();
  }

  public Tag getRandomUserTag(UUID userId) {
    List<Tag> userTags = getUserTags(userId);
    return userTags.get(RandomUtils.randomInteger(0, userTags.size() - 1));
  }

  public List<Transaction> getTransactions() {
    return transactions;
  }

  public List<Transfer> getTransfers() {
    return transfers;
  }

  public List<Subscription> getSubscriptions() {
    return subscriptions;
  }

  public List<Transaction> getTransactions(UUID userId) {
    return transactions.stream().filter(t -> t.getUserId().equals(userId)).toList();
  }

  public List<Transaction> getTransactionsByAccountId(UUID accountId) {
    return transactions.stream().filter(t -> t.getAccountId().equals(accountId)).toList();
  }

  public List<Transaction> getTransactionsByCategoryId(UUID categoryId) {
    return transactions.stream()
        .filter(t -> t.getCategoryId() != null && t.getCategoryId().equals(categoryId))
        .toList();
  }

  public TransactionRequestDto createTransactionRequest(UUID userId) {
    Transaction transaction = TransactionFactory.fakeTransaction(userId);
    return new TransactionRequestDto(
        transaction.getUserId(),
        List.of(
            new TransactionRequestItemDto(
                transaction.getAccountId(),
                transaction.getCategoryId(),
                transaction.getDate(),
                transaction.getDescription(),
                transaction.getAmount(),
                transaction.getCurrency(),
                null)));
  }

  public TransferRequestDto createTransferRequest(UUID userId) {
    List<UUID> userAccounts =
        transactions.stream()
            .filter(t -> t.getUserId().equals(userId))
            .map(Transaction::getAccountId)
            .toList();
    UUID fromAccount = userAccounts.get(0);
    UUID toAccount = userAccounts.get(1);
    return new TransferRequestDto(
        userId,
        fromAccount,
        toAccount,
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomLocalDate(2025, 2025),
        RandomUtils.randomString(3).toUpperCase());
  }

  public SubscriptionRequestDto createSubscriptionRequest(UUID userId) {
    List<UUID> userAccounts =
        transactions.stream()
            .filter(t -> t.getUserId().equals(userId))
            .map(Transaction::getAccountId)
            .toList();
    List<UUID> categoryAccounts =
        transactions.stream()
            .filter(t -> t.getUserId().equals(userId))
            .map(Transaction::getCategoryId)
            .toList();
    LocalDate startDate = RandomUtils.randomLocalDate(2024, 2025);
    LocalDate endDate =
        Math.random() < 0.5 ? null : startDate.plusMonths(RandomUtils.randomInteger(3, 24));
    return new SubscriptionRequestDto(
        userId,
        userAccounts.get(RandomUtils.randomInteger(0, userAccounts.size() - 1)),
        categoryAccounts.get(RandomUtils.randomInteger(0, categoryAccounts.size() - 1)),
        RandomUtils.randomString(25),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomString(3).toUpperCase(),
        new RecurrenceDto("FREQ=MONTHLY", startDate, endDate));
  }

  public TransferUpdateRequestDto createTransferUpdateRequest(UUID userId) {
    List<UUID> userAccounts =
        transactions.stream()
            .filter(t -> t.getUserId().equals(userId))
            .map(Transaction::getAccountId)
            .toList();
    UUID fromAccount = userAccounts.get(0);
    UUID toAccount = userAccounts.get(1);
    return new TransferUpdateRequestDto(
        fromAccount,
        toAccount,
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomLocalDate(2025, 2025),
        RandomUtils.randomString(3).toUpperCase());
  }

  public TransactionDto getExpectedTransactionDto(TransactionRequestDto request) {
    TransactionRequestItemDto item = request.transactions().getFirst();
    return TransactionDto.builder()
        .withUserId(request.userId())
        .withAccountId(item.accountId())
        .withCategoryId(item.categoryId())
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
                criteria.getCategories().isEmpty()
                    || criteria.getCategories().get().contains(transaction.getCategoryId()))
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
                criteria
                    .getTags()
                    .map(
                        tagIds -> {
                          if (tagIds.isEmpty()) {
                            return true;
                          }
                          long distinctCount = tagIds.stream().distinct().count();
                          if (distinctCount > 1) {
                            return false;
                          }
                          Object requiredTag = tagIds.stream().findFirst().get();
                          return transaction.getTags().stream()
                              .anyMatch(tag -> tag.getTagId().equals(requiredTag));
                        })
                    .orElse(true))
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
        transactions.stream()
            .filter(t -> t.getUserId().equals(userId) && t.getTransfer() == null)
            .toList();
    return userTransactions.get(RandomUtils.randomInteger(0, userTransactions.size() - 1));
  }

  public Set<UUID> getRandomTag(UUID userId) {
    List<Tag> userTags = tags.stream().filter(t -> t.getUserId().equals(userId)).toList();
    return userTags.isEmpty()
        ? null
        : Collections.singleton(
            userTags.get(RandomUtils.randomInteger(0, userTags.size() - 1)).getTagId());
  }

  public Transfer getRandomTransferByUser(UUID userId) {
    List<Transaction> userTransactions =
        transactions.stream()
            .filter(transaction -> transaction.getUserId().equals(userId))
            .toList();
    List<Transfer> userTransfer =
        transfers.stream()
            .filter(transfer -> userTransactions.contains(transfer.getTransactionFrom()))
            .toList();
    return userTransfer.get(RandomUtils.randomInteger(0, userTransfer.size() - 1));
  }

  @Override
  public TransactionTestContext self() {
    return this;
  }

  @Override
  public TransactionTestContext generateScript(Path dir) {
    List<Transaction> transactionsNoRelationship = new ArrayList<>(this.transactions);
    transactionsNoRelationship.forEach(
        t -> {
          t.setTransfer(null);
          t.setSubscription(null);
        });
    EntityScriptGenerator scriptGenerator =
        new EntityScriptGenerator(
            new ScriptMetadata(dir, tags, transactionsNoRelationship, transfers, subscriptions),
            new SQLScriptService());
    StringBuilder script = scriptGenerator.generateScript();
    for (Transfer transfer : this.transfers) {
      Transaction transactionFrom = transfer.getTransactionFrom();
      Transaction transactionTo = transfer.getTransactionTo();

      script
          .append("\nUPDATE TRANSACTIONS SET TRANSFER_ID = '")
          .append(transfer.getTransferId())
          .append("' WHERE TRANSACTION_ID = '")
          .append(transactionFrom.getTransactionId())
          .append("';");

      script
          .append("\nUPDATE TRANSACTIONS SET TRANSFER_ID = '")
          .append(transfer.getTransferId())
          .append("' WHERE TRANSACTION_ID = '")
          .append(transactionTo.getTransactionId())
          .append("';");
    }
    for (Subscription subscription : this.subscriptions) {
      for (Transaction transaction : subscription.getTransactions()) {
        script
            .append("\nUPDATE TRANSACTIONS SET SUBSCRIPTION_ID = '")
            .append(subscription.getSubscriptionId())
            .append("' WHERE TRANSACTION_ID = '")
            .append(transaction.getTransactionId())
            .append("';");
      }
    }
    scriptGenerator.save(script);
    return self();
  }
}
