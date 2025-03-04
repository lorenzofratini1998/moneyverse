package it.moneyverse.transaction.model;

import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.model.TestFactory;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.enums.TransactionSortAttributeEnum;
import it.moneyverse.transaction.model.dto.TransactionCriteria;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import it.moneyverse.transaction.model.dto.TransactionRequestItemDto;
import it.moneyverse.transaction.model.dto.TransactionUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.entities.Transaction;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;

public class TransactionTestFactory {
  private static final Logger LOGGER = LoggerFactory.getLogger(TransactionTestFactory.class);
  private static final Supplier<UUID> FAKE_ACCOUNT_ID_SUPPLIER = RandomUtils::randomUUID;
  private static final Supplier<UUID> FAKE_CATEGORY_ID_SUPPLIER = RandomUtils::randomUUID;
  private static final Supplier<UUID> FAKE_BUDGET_ID_SUPPLIER = RandomUtils::randomUUID;
  private static final Supplier<LocalDate> FAKE_DATE_SUPPLIER = RandomUtils::randomDate;
  private static final Supplier<String> FAKE_DESCRIPTION_SUPPLIER =
      () -> RandomUtils.randomString(30);
  private static final Supplier<BigDecimal> FAKE_AMOUNT_SUPPLIER = RandomUtils::randomBigDecimal;
  private static final Supplier<String> FAKE_CURRENCY_SUPPLIER = RandomUtils::randomCurrency;
  private static final Supplier<UUID> FAKE_TAG_ID_SUPPLIER = RandomUtils::randomUUID;

  public static List<Transaction> createTransactions(List<UserModel> users, List<Tag> tags) {
    List<Transaction> transactions =
        users.stream()
            .map(user -> fakeUserTransaction(user.getUserId(), tags))
            .flatMap(List::stream)
            .collect(Collectors.toList());
    LOGGER.info("Created {} random transactions for testing", transactions.size());
    return transactions;
  }

  private static List<Transaction> fakeUserTransaction(UUID userId, List<Tag> tags) {
    List<UUID> accounts = randomAccounts();
    List<UUID> categories = randomCategories();
    List<Tag> userTags = tags.stream().filter(tag -> tag.getUserId().equals(userId)).toList();
    return IntStream.range(
            0,
            RandomUtils.randomInteger(
                TestFactory.MIN_TRANSACTION_PER_USER, TestFactory.MAX_TRANSACTION_PER_USER))
        .mapToObj(
            i ->
                RandomUtils.flipCoin() && !userTags.isEmpty()
                    ? fakeTransaction(
                        userId,
                        Set.of(userTags.get(RandomUtils.randomInteger(userTags.size()))),
                        accounts,
                        categories)
                    : fakeTransaction(userId, accounts, categories))
        .toList();
  }

  private static List<UUID> randomAccounts() {
    return IntStream.range(
            0,
            RandomUtils.randomInteger(
                TestFactory.MIN_ACCOUNTS_PER_USER, TestFactory.MAX_ACCOUNTS_PER_USER))
        .mapToObj(i -> RandomUtils.randomUUID())
        .toList();
  }

  private static List<UUID> randomCategories() {
    return IntStream.range(
            0,
            RandomUtils.randomInteger(
                TestFactory.MIN_CATEGORIES_PER_USER, TestFactory.MAX_CATEGORIES_PER_USER))
        .mapToObj(i -> RandomUtils.randomUUID())
        .toList();
  }

  public static Transaction fakeTransaction(
      UUID userId, Set<Tag> tags, List<UUID> accounts, List<UUID> categories) {
    Transaction transaction = fakeTransaction(userId, accounts, categories);
    transaction.setTags(tags);
    return transaction;
  }

  public static Transaction fakeTransaction(
      UUID userId, List<UUID> accounts, List<UUID> categories) {
    Transaction transaction = fakeTransaction(userId);
    transaction.setAccountId(accounts.get(RandomUtils.randomInteger(accounts.size())));
    transaction.setCategoryId(categories.get(RandomUtils.randomInteger(categories.size())));
    return transaction;
  }

  public static Transaction fakeTransaction(UUID userId) {
    Transaction transaction = new Transaction();
    transaction.setTransactionId(RandomUtils.randomUUID());
    transaction.setUserId(userId);
    transaction.setDate(FAKE_DATE_SUPPLIER.get());
    transaction.setDescription(FAKE_DESCRIPTION_SUPPLIER.get());
    transaction.setAmount(FAKE_AMOUNT_SUPPLIER.get());
    transaction.setNormalizedAmount(FAKE_AMOUNT_SUPPLIER.get());
    transaction.setCurrency(FAKE_CURRENCY_SUPPLIER.get());
    transaction.setCreatedBy(TestFactory.FAKE_USER);
    transaction.setCreatedAt(LocalDateTime.now());
    transaction.setUpdatedBy(TestFactory.FAKE_USER);
    transaction.setUpdatedAt(LocalDateTime.now());
    transaction.setAccountId(FAKE_ACCOUNT_ID_SUPPLIER.get());
    transaction.setCategoryId(FAKE_CATEGORY_ID_SUPPLIER.get());
    transaction.setBudgetId(FAKE_BUDGET_ID_SUPPLIER.get());
    return transaction;
  }

  public static class TransactionRequestBuilder {
    private UUID userId = RandomUtils.randomUUID();
    private UUID accountId = FAKE_ACCOUNT_ID_SUPPLIER.get();
    private final UUID categoryId = FAKE_CATEGORY_ID_SUPPLIER.get();
    private LocalDate date = FAKE_DATE_SUPPLIER.get();
    private final String description = FAKE_DESCRIPTION_SUPPLIER.get();
    private BigDecimal amount = FAKE_AMOUNT_SUPPLIER.get();
    private String currency = FAKE_CURRENCY_SUPPLIER.get();
    private Set<UUID> tagIds = Set.of(FAKE_TAG_ID_SUPPLIER.get());

    public TransactionRequestBuilder withUserId(UUID userId) {
      this.userId = userId;
      return this;
    }

    public TransactionRequestBuilder withNullUserId() {
      this.userId = null;
      return this;
    }

    public TransactionRequestBuilder withNullAccountId() {
      this.accountId = null;
      return this;
    }

    public TransactionRequestBuilder withNullDate() {
      this.date = null;
      return this;
    }

    public TransactionRequestBuilder withNullAmount() {
      this.amount = null;
      return this;
    }

    public TransactionRequestBuilder withNullCurrency() {
      this.currency = null;
      return this;
    }

    public TransactionRequestBuilder withEmptyTags() {
      this.tagIds = Set.of();
      return this;
    }

    public static Stream<Supplier<TransactionRequestDto>> invalidTransactionRequestProvider() {
      return Stream.of(
          () -> builder().withNullUserId().build(),
          () -> builder().withNullAccountId().build(),
          () -> builder().withNullDate().build(),
          () -> builder().withNullAmount().build(),
          () -> builder().withNullCurrency().build());
    }

    public static TransactionRequestDto defaultInstance() {
      return builder().build();
    }

    public static TransactionRequestBuilder builder() {
      return new TransactionRequestBuilder();
    }

    public TransactionRequestDto build() {
      return new TransactionRequestDto(
          userId,
          List.of(
              new TransactionRequestItemDto(
                  accountId, categoryId, date, description, amount, currency, tagIds)));
    }
  }

  public static class TransactionUpdateRequestBuilder {
    private final UUID accountId = FAKE_ACCOUNT_ID_SUPPLIER.get();
    private final UUID categoryId = FAKE_CATEGORY_ID_SUPPLIER.get();
    private final LocalDate date = FAKE_DATE_SUPPLIER.get();
    private final String description = FAKE_DESCRIPTION_SUPPLIER.get();
    private final BigDecimal amount = FAKE_AMOUNT_SUPPLIER.get();
    private final String currency = FAKE_CURRENCY_SUPPLIER.get();
    private Set<UUID> tagIds = Set.of();

    public TransactionUpdateRequestBuilder withTags(Set<UUID> tags) {
      this.tagIds = tags;
      return this;
    }

    public static TransactionUpdateRequestDto defaultInstance() {
      return builder().build();
    }

    public static TransactionUpdateRequestBuilder builder() {
      return new TransactionUpdateRequestBuilder();
    }

    public TransactionUpdateRequestDto build() {
      return new TransactionUpdateRequestDto(
          accountId, categoryId, date, description, amount, currency, tagIds);
    }
  }

  public static class TransactionCriteriaBuilder {
    private TransactionTestContext testContext;
    private UUID userId;

    public static TransactionCriteriaBuilder generator(
        UUID userId, TransactionTestContext testContext) {
      TransactionCriteriaBuilder builder = new TransactionCriteriaBuilder();
      builder.testContext = testContext;
      builder.userId = userId;
      return builder;
    }

    public TransactionCriteria generate() {
      return composeCriteria().apply(new TransactionCriteria());
    }

    private Function<TransactionCriteria, TransactionCriteria> composeCriteria() {
      return withRandomAccounts()
          .andThen(withRandomCategories())
          .andThen(withRandomDate())
          .andThen(withRandomAmount())
          .andThen(withRandomTags())
          .andThen(withPage())
          .andThen(withSort());
    }

    private Function<TransactionCriteria, TransactionCriteria> withRandomAccounts() {
      return criteria -> {
        criteria.setAccounts(RandomUtils.flipCoin() ? randomAccounts() : null);
        return criteria;
      };
    }

    private List<UUID> randomAccounts() {
      List<UUID> accounts =
          testContext.getTransactions().stream()
              .filter(transaction -> transaction.getUserId().equals(userId))
              .map(Transaction::getAccountId)
              .toList();
      List<UUID> randomAccounts = new ArrayList<>();
      for (int i = 0;
          i
              < RandomUtils.randomInteger(
                  TestFactory.MIN_ACCOUNTS_PER_USER, TestFactory.MAX_ACCOUNTS_PER_USER);
          i++) {
        randomAccounts.add(accounts.get(RandomUtils.randomInteger(accounts.size())));
      }
      return randomAccounts;
    }

    private Function<TransactionCriteria, TransactionCriteria> withRandomCategories() {
      return criteria -> {
        criteria.setCategories(RandomUtils.flipCoin() ? randomCategories() : null);
        return criteria;
      };
    }

    private List<UUID> randomCategories() {
      List<UUID> categories =
          testContext.getTransactions().stream()
              .filter(transaction -> transaction.getUserId().equals(userId))
              .map(Transaction::getCategoryId)
              .toList();
      List<UUID> randomCategories = new ArrayList<>();
      for (int i = 0;
          i
              < RandomUtils.randomInteger(
                  TestFactory.MIN_CATEGORIES_PER_USER, TestFactory.MAX_CATEGORIES_PER_USER);
          i++) {
        randomCategories.add(categories.get(RandomUtils.randomInteger(categories.size())));
      }
      return randomCategories;
    }

    private Function<TransactionCriteria, TransactionCriteria> withRandomDate() {
      return criteria -> {
        criteria.setDate(RandomUtils.flipCoin() ? TestFactory.fakeDateCriteria() : null);
        return criteria;
      };
    }

    private Function<TransactionCriteria, TransactionCriteria> withRandomAmount() {
      return criteria -> {
        criteria.setAmount(
            RandomUtils.flipCoin()
                ? TestFactory.fakeBoundCriteria(
                    testContext.getTransactions().stream().map(Transaction::getAmount).toList())
                : null);
        return criteria;
      };
    }

    private Function<TransactionCriteria, TransactionCriteria> withRandomTags() {
      return criteria -> {
        criteria.setTags(RandomUtils.flipCoin() ? randomTags() : null);
        return criteria;
      };
    }

    private List<UUID> randomTags() {
      List<UUID> tags =
          testContext.getTransactions().stream()
              .filter(transaction -> transaction.getUserId().equals(userId))
              .flatMap(t -> t.getTags().stream().map(Tag::getTagId))
              .toList();
      if (tags.isEmpty()) {
        return Collections.emptyList();
      }
      List<UUID> randomTags = new ArrayList<>();
      for (int i = 0;
          i
              < RandomUtils.randomInteger(
                  TestFactory.MIN_TAGS_PER_USER, TestFactory.MAX_TAGS_PER_USER);
          i++) {
        randomTags.add(tags.get(RandomUtils.randomInteger(tags.size())));
      }
      return randomTags;
    }

    private Function<TransactionCriteria, TransactionCriteria> withPage() {
      return criteria -> {
        criteria.setPage(new PageCriteria());
        return criteria;
      };
    }

    private Function<TransactionCriteria, TransactionCriteria> withSort() {
      return criteria -> {
        criteria.setSort(
            new SortCriteria<>(TransactionSortAttributeEnum.DATE, Sort.Direction.DESC));
        return criteria;
      };
    }
  }
}
