package it.moneyverse.transaction.model;

import static it.moneyverse.transaction.enums.TransactionSortAttributeEnum.*;

import it.moneyverse.core.model.dto.BoundCriteria;
import it.moneyverse.core.model.dto.DateCriteria;
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
import java.util.function.Predicate;
import org.springframework.data.domain.Sort;

public class TransactionTestContext extends TestContext<TransactionTestContext> {

  private static TransactionTestContext currentInstance;

  @TestModelEntity private final List<Tag> tags;
  @TestModelEntity private final List<Transaction> transactions;
  @TestModelEntity private final List<Transfer> transfers;
  @TestModelEntity private final List<Subscription> subscriptions;

  public TransactionTestContext(KeycloakContainer keycloakContainer) {
    super(keycloakContainer);
    this.tags = TagTestFactory.createTags(getUsers());
    this.transactions = TransactionTestFactory.createTransactions(getUsers(), tags);
    this.transfers = TransferTestFactory.createTransfers(getUsers(), transactions);
    this.subscriptions = SubscriptionTestFactory.createSubscriptions(getUsers(), transactions);
    setCurrentInstance(this);
  }

  public TransactionTestContext() {
    super();
    this.tags = TagTestFactory.createTags(getUsers());
    this.transactions = TransactionTestFactory.createTransactions(getUsers(), tags);
    this.transfers = TransferTestFactory.createTransfers(getUsers(), transactions);
    this.subscriptions = SubscriptionTestFactory.createSubscriptions(getUsers(), transactions);
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

  public List<Subscription> getUserSubscription(UUID userId) {
    return subscriptions.stream()
        .filter(subscription -> subscription.getUserId().equals(userId))
        .toList();
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
    return TransactionTestFactory.TransactionRequestBuilder.builder()
        .withUserId(userId)
        .withEmptyTags()
        .build();
  }

  public TransferRequestDto createTransferRequest(UUID userId) {
    List<UUID> userAccounts =
        transactions.stream()
            .filter(t -> t.getUserId().equals(userId))
            .map(Transaction::getAccountId)
            .distinct()
            .toList();
    UUID fromAccount = userAccounts.get(0);
    UUID toAccount = userAccounts.get(1);
    return TransferTestFactory.TransferRequestBuilder.builder()
        .withUserId(userId)
        .withFromAccount(fromAccount)
        .withToAccount(toAccount)
        .build();
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
    LocalDate startDate = RandomUtils.randomDate();
    LocalDate endDate =
        RandomUtils.flipCoin() ? null : startDate.plusMonths(RandomUtils.randomInteger(3, 12));
    return SubscriptionTestFactory.SubscriptionRequestBuilder.builder()
        .withUserId(userId)
        .withAccountId(userAccounts.get(RandomUtils.randomInteger(userAccounts.size())))
        .withCategoryId(categoryAccounts.get(RandomUtils.randomInteger(categoryAccounts.size())))
        .withRecurrence(startDate, endDate)
        .build();
  }

  public TransferUpdateRequestDto createTransferUpdateRequest(UUID userId) {
    List<UUID> userAccounts =
        transactions.stream()
            .filter(t -> t.getUserId().equals(userId))
            .map(Transaction::getAccountId)
            .toList();
    UUID fromAccount = userAccounts.get(0);
    UUID toAccount = userAccounts.get(1);
    return TransferTestFactory.TransferUpdateRequestBuilder.builder()
        .withFromAccount(fromAccount)
        .withToAccount(toAccount)
        .build();
  }

  public List<Transaction> filterTransactions(UUID userId, TransactionCriteria criteria) {
    return transactions.stream()
        .filter(byUserId(userId))
        .filter(byAccounts(criteria.getAccounts()))
        .filter(byCategories(criteria.getCategories()))
        .filter(byDate(criteria.getDate()))
        .filter(byAmount(criteria.getAmount()))
        .filter(byTags(criteria.getTags()))
        .sorted(sortByCriteria(criteria.getSort()))
        .skip(criteria.getPage().getOffset())
        .limit(criteria.getPage().getLimit())
        .toList();
  }

  private Predicate<Transaction> byUserId(UUID userId) {
    return transaction -> transaction.getUserId().equals(userId);
  }

  private Predicate<Transaction> byAccounts(Optional<List<UUID>> accounts) {
    return transaction ->
        accounts.map(accountList -> accountList.contains(transaction.getAccountId())).orElse(true);
  }

  private Predicate<Transaction> byCategories(Optional<List<UUID>> categories) {
    return transaction ->
        categories
            .map(categoryList -> categoryList.contains(transaction.getCategoryId()))
            .orElse(true);
  }

  private Predicate<Transaction> byDate(Optional<DateCriteria> dateBound) {
    return transaction ->
        dateBound
            .map(
                date -> {
                  boolean matchesStart =
                      date.getStart().map(min -> !transaction.getDate().isBefore(min)).orElse(true);
                  boolean matchesEnd =
                      date.getEnd().map(max -> !transaction.getDate().isAfter(max)).orElse(true);
                  return matchesStart && matchesEnd;
                })
            .orElse(true);
  }

  private Predicate<Transaction> byAmount(Optional<BoundCriteria> amount) {
    return transaction ->
        amount
            .map(
                amountCriteria ->
                    transaction.getAmount() != null
                        && filterByBound(transaction.getAmount(), amountCriteria))
            .orElse(true);
  }

  private Predicate<Transaction> byTags(Optional<List<UUID>> tags) {
    return transaction ->
        tags.map(
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
            .orElse(true);
  }

  private Comparator<Transaction> sortByCriteria(
      SortCriteria<TransactionSortAttributeEnum> sortCriteria) {
    if (sortCriteria == null) {
      return (a, b) -> 0;
    }

    Comparator<Transaction> comparator =
        switch (sortCriteria.getAttribute()) {
          case DATE -> Comparator.comparing(Transaction::getDate);
          case AMOUNT -> Comparator.comparing(Transaction::getAmount);
          default -> (a, b) -> 0;
        };

    return sortCriteria.getDirection() == Sort.Direction.ASC ? comparator : comparator.reversed();
  }

  public TransactionCriteria createTransactionCriteria(UUID userId) {
    return TransactionTestFactory.TransactionCriteriaBuilder.generator(userId, getCurrentInstance())
        .generate();
  }

  public Transaction getRandomTransaction(UUID userId) {
    List<Transaction> userTransactions =
        transactions.stream()
            .filter(t -> t.getUserId().equals(userId) && t.getTransfer() == null)
            .toList();
    return userTransactions.get(RandomUtils.randomInteger(userTransactions.size()));
  }

  public Set<UUID> getRandomTag(UUID userId) {
    List<Tag> userTags = tags.stream().filter(t -> t.getUserId().equals(userId)).toList();
    return userTags.isEmpty()
        ? null
        : Collections.singleton(
            userTags.get(RandomUtils.randomInteger(userTags.size())).getTagId());
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
    return userTransfer.get(RandomUtils.randomInteger(userTransfer.size()));
  }

  public Subscription getRandomSubscriptionByUser(UUID userId) {
    List<Subscription> userSubscriptions =
        subscriptions.stream()
            .filter(subscription -> subscription.getUserId().equals(userId))
            .toList();
    return userSubscriptions.get(RandomUtils.randomInteger(userSubscriptions.size()));
  }

  @Override
  public TransactionTestContext self() {
    return this;
  }

  @Override
  public TransactionTestContext generateScript(Path dir) {
    List<Transaction> transactionsWithoutRelationships =
        this.transactions.stream().map(Transaction::copy).toList();
    transactionsWithoutRelationships.forEach(
        t -> {
          t.setTransfer(null);
          t.setSubscription(null);
        });
    EntityScriptGenerator scriptGenerator =
        new EntityScriptGenerator(
            new ScriptMetadata(
                dir, tags, transactionsWithoutRelationships, transfers, subscriptions),
            new SQLScriptService());
    StringBuilder script = scriptGenerator.generateScript();
    transfers.forEach(transfer -> appendTransferUpdates(script, transfer));
    subscriptions.forEach(subscription -> appendSubscriptionUpdates(script, subscription));
    scriptGenerator.save(script);
    return self();
  }

  private void appendTransferUpdates(StringBuilder script, Transfer transfer) {
    Transaction transactionFrom = transfer.getTransactionFrom();
    Transaction transactionTo = transfer.getTransactionTo();
    appendUpdateStatement(
        script,
        "TRANSFER_ID",
        transfer.getTransferId().toString(),
        transactionFrom.getTransactionId().toString());
    appendUpdateStatement(
        script,
        "TRANSFER_ID",
        transfer.getTransferId().toString(),
        transactionTo.getTransactionId().toString());
  }

  private void appendSubscriptionUpdates(StringBuilder script, Subscription subscription) {
    subscription
        .getTransactions()
        .forEach(
            transaction ->
                appendUpdateStatement(
                    script,
                    "SUBSCRIPTION_ID",
                    subscription.getSubscriptionId().toString(),
                    transaction.getTransactionId().toString()));
  }

  private void appendUpdateStatement(
      StringBuilder script, String column, String idValue, String transactionId) {
    String update =
        String.format(
            "%nUPDATE TRANSACTIONS SET %s = '%s' WHERE TRANSACTION_ID = '%s';",
            column, idValue, transactionId);
    script.append(update);
  }
}
