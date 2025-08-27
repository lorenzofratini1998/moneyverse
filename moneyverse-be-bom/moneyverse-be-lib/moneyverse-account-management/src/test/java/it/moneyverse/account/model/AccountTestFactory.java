package it.moneyverse.account.model;

import it.moneyverse.account.enums.AccountSortAttributeEnum;
import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.dto.AccountUpdateRequestDto;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.entities.AccountCategory;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.core.model.dto.StyleRequestDto;
import it.moneyverse.core.model.entities.UserModel;
import it.moneyverse.test.model.TestFactory;
import it.moneyverse.test.utils.RandomUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;

public class AccountTestFactory {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountTestFactory.class);
  private static final Supplier<UUID> FAKE_ACCOUNT_ID = RandomUtils::randomUUID;
  private static final Supplier<UUID> FAKE_USER_ID = RandomUtils::randomUUID;
  private static final Supplier<String> FAKE_ACCOUNT_NAME = () -> RandomUtils.randomString(15);
  private static final Supplier<BigDecimal> FAKE_BALANCE = RandomUtils::randomBigDecimal;
  private static final Supplier<BigDecimal> FAKE_BALANCE_TARGET = RandomUtils::randomBigDecimal;
  private static final Supplier<String> FAKE_ACCOUNT_CATEGORY_NAME =
      () -> RandomUtils.randomString(15).toUpperCase();
  private static final Supplier<String> FAKE_DESCRIPTION = () -> RandomUtils.randomString(30);
  private static final Supplier<String> FAKE_CURRENCY = RandomUtils::randomCurrency;
  private static final Supplier<Boolean> FAKE_DEFAULT = RandomUtils::randomBoolean;
  private static final Supplier<StyleRequestDto> FAKE_STYLE_REQUEST =
      () -> new StyleRequestDto(RandomUtils.randomString(10), RandomUtils.randomString(10));

  public static List<AccountCategory> createAccountCategories() {
    List<AccountCategory> accountCategories = new ArrayList<>();
    for (int i = 0; i < TestFactory.ACCOUNT_CATEGORY_NUMBER; i++) {
      accountCategories.add(AccountTestFactory.fakeAccountCategory(i));
    }
    return accountCategories;
  }

  private static AccountCategory fakeAccountCategory(int counter) {
    counter = counter + 1;
    AccountCategory category = new AccountCategory();
    category.setAccountCategoryId((long) counter);
    category.setName("CATEGORY %s".formatted(counter));
    category.setDescription(RandomUtils.randomString(20));
    category.setStyle(TestFactory.fakeStyle());
    return category;
  }

  public static List<Account> createAccounts(
      List<UserModel> users, List<AccountCategory> categories) {
    List<Account> accounts = new ArrayList<>();
    for (UserModel user : users) {
      int numAccountsPerUser =
          RandomUtils.randomInteger(
              TestFactory.MIN_ACCOUNTS_PER_USER, TestFactory.MAX_ACCOUNTS_PER_USER);
      for (int i = 0; i < numAccountsPerUser; i++) {
        AccountCategory category =
            categories.get(RandomUtils.randomInteger(0, categories.size() - 1));
        accounts.add(AccountTestFactory.fakeAccount(user.getUserId(), category, i));
      }
    }
    LOGGER.info("Created {} random accounts for testing", accounts.size());
    return accounts;
  }

  private static Account fakeAccount(UUID userId, AccountCategory category, Integer counter) {
    counter = counter + 1;
    Account account = new Account();
    account.setAccountId(RandomUtils.randomUUID());
    account.setUserId(userId);
    account.setAccountName("Account %s".formatted(counter));
    account.setBalance(
        RandomUtils.randomDecimal(0.0, Math.random() * 1000).setScale(2, RoundingMode.HALF_EVEN));
    account.setBalanceTarget(
        RandomUtils.flipCoin()
            ? RandomUtils.randomDecimal(0.0, Math.random() * 2000)
                .setScale(2, RoundingMode.HALF_EVEN)
            : null);
    account.setAccountCategory(category);
    account.setAccountDescription("Account Description %s".formatted(counter));
    account.setCurrency(FAKE_CURRENCY.get());
    account.setDefault(counter == 1);
    account.setStyle(TestFactory.fakeStyle());
    account.setCreatedBy(TestFactory.FAKE_USER);
    account.setCreatedAt(LocalDateTime.now());
    account.setUpdatedBy(TestFactory.FAKE_USER);
    account.setUpdatedAt(LocalDateTime.now());
    return account;
  }

  public static Account fakeAccount() {
    Account account = new Account();
    AccountCategory category = fakeAccountCategory();
    category.setName(FAKE_ACCOUNT_CATEGORY_NAME.get());
    account.setAccountId(FAKE_ACCOUNT_ID.get());
    account.setUserId(FAKE_USER_ID.get());
    account.setAccountName(FAKE_ACCOUNT_NAME.get());
    account.setBalance(FAKE_BALANCE.get());
    account.setBalanceTarget(FAKE_BALANCE_TARGET.get());
    account.setAccountCategory(category);
    account.setAccountDescription(FAKE_DESCRIPTION.get());
    account.setCurrency(FAKE_CURRENCY.get());
    account.setDefault(FAKE_DEFAULT.get());
    account.setStyle(TestFactory.fakeStyle());
    return account;
  }

  public static AccountCategory fakeAccountCategory() {
    AccountCategory category = new AccountCategory();
    category.setAccountCategoryId(RandomUtils.randomBigDecimal().longValue());
    category.setName(FAKE_ACCOUNT_CATEGORY_NAME.get());
    category.setDescription(FAKE_DESCRIPTION.get());
    category.setStyle(TestFactory.fakeStyle());
    return category;
  }

  public static class AccountRequestDtoBuilder {
    private UUID userId = FAKE_USER_ID.get();
    private String accountName = FAKE_ACCOUNT_NAME.get();
    private final BigDecimal balance = RandomUtils.flipCoin() ? FAKE_BALANCE.get() : null;
    private final BigDecimal balanceTarget =
        RandomUtils.flipCoin() ? FAKE_BALANCE_TARGET.get() : null;
    private String accountCategory = FAKE_ACCOUNT_CATEGORY_NAME.get();
    private final String accountDescription =
        RandomUtils.flipCoin() ? FAKE_DESCRIPTION.get() : null;
    private String currency = FAKE_CURRENCY.get();
    private StyleRequestDto style = FAKE_STYLE_REQUEST.get();

    public AccountRequestDtoBuilder withUserId(UUID userId) {
      this.userId = userId;
      return this;
    }

    public static Stream<Supplier<AccountRequestDto>> invalidAccountRequestProvider() {
      return Stream.of(
          () -> AccountRequestDtoBuilder.builder().withNullUserId().build(),
          () -> AccountRequestDtoBuilder.builder().withNullAccountName().build(),
          () -> AccountRequestDtoBuilder.builder().withNullAccountCategory().build(),
          () -> AccountRequestDtoBuilder.builder().withNullCurrency().build());
    }

    private AccountRequestDtoBuilder withNullUserId() {
      this.userId = null;
      return this;
    }

    private AccountRequestDtoBuilder withNullAccountName() {
      this.accountName = null;
      return this;
    }

    private AccountRequestDtoBuilder withNullAccountCategory() {
      this.accountCategory = null;
      return this;
    }

    private AccountRequestDtoBuilder withNullCurrency() {
      this.currency = null;
      return this;
    }

    public AccountRequestDtoBuilder withAccountCategory(String accountCategory) {
      this.accountCategory = accountCategory;
      return this;
    }

    public static AccountRequestDtoBuilder builder() {
      return new AccountRequestDtoBuilder();
    }

    public static AccountRequestDto defaultInstance() {
      return builder().build();
    }

    public AccountRequestDto build() {
      return new AccountRequestDto(
          userId,
          accountName,
          balance,
          balanceTarget,
          accountCategory,
          accountDescription,
          currency,
          style);
    }
  }

  public static class AccountUpdateRequestDtoBuilder {
    private final String accountName = FAKE_ACCOUNT_NAME.get();
    private final BigDecimal balance = FAKE_BALANCE.get();
    private final BigDecimal balanceTarget = FAKE_BALANCE_TARGET.get();
    private String accountCategory = FAKE_ACCOUNT_CATEGORY_NAME.get();
    private final String accountDescription = FAKE_DESCRIPTION.get();
    private Boolean isDefault = FAKE_DEFAULT.get();
    private StyleRequestDto style = FAKE_STYLE_REQUEST.get();

    public AccountUpdateRequestDtoBuilder withAccountCategory(String accountCategory) {
      this.accountCategory = accountCategory;
      return this;
    }

    public AccountUpdateRequestDtoBuilder withDefault(Boolean isDefault) {
      this.isDefault = isDefault;
      return this;
    }

    public static AccountUpdateRequestDtoBuilder builder() {
      return new AccountUpdateRequestDtoBuilder();
    }

    public static AccountUpdateRequestDto defaultInstance() {
      return builder().build();
    }

    public AccountUpdateRequestDto build() {
      return new AccountUpdateRequestDto(
          accountName,
          balance,
          balanceTarget,
          accountCategory,
          accountDescription,
          isDefault,
          style);
    }
  }

  public static class AccountCriteriaBuilder {
    private AccountTestContext testContext;

    private AccountCriteriaBuilder() {}

    public static AccountCriteriaBuilder generator(AccountTestContext testContext) {
      AccountCriteriaBuilder builder = new AccountCriteriaBuilder();
      builder.testContext = testContext;
      return builder;
    }

    public AccountCriteria generate() {
      return composeCriteria().apply(new AccountCriteria());
    }

    private Function<AccountCriteria, AccountCriteria> composeCriteria() {
      return withRandomBalance()
          .andThen(withRandomBalanceTarget())
          .andThen(withRandomBalanceTarget())
          .andThen(withRandomAccountCategory())
          .andThen(withRandomCurrency())
          .andThen(withRandomIsDefault())
          .andThen(withPage())
          .andThen(withSort());
    }

    private Function<AccountCriteria, AccountCriteria> withRandomBalance() {
      return criteria -> {
        criteria.setBalance(
            RandomUtils.flipCoin()
                ? TestFactory.fakeBoundCriteria(
                    testContext.getAccounts().stream().map(Account::getBalance).toList())
                : null);
        return criteria;
      };
    }

    private Function<AccountCriteria, AccountCriteria> withRandomBalanceTarget() {
      return criteria -> {
        criteria.setBalanceTarget(
            RandomUtils.flipCoin()
                ? TestFactory.fakeBoundCriteria(
                    testContext.getAccounts().stream().map(Account::getBalanceTarget).toList())
                : null);
        return criteria;
      };
    }

    private Function<AccountCriteria, AccountCriteria> withRandomAccountCategory() {
      return criteria -> {
        criteria.setAccountCategories(
            RandomUtils.flipCoin()
                ? List.of(testContext.getRandomAccountCategory().getName())
                : null);
        return criteria;
      };
    }

    private Function<AccountCriteria, AccountCriteria> withRandomCurrency() {
      return criteria -> {
        criteria.setCurrencies(
            RandomUtils.flipCoin() ? List.of(RandomUtils.randomCurrency()) : null);
        return criteria;
      };
    }

    private Function<AccountCriteria, AccountCriteria> withRandomIsDefault() {
      return criteria -> {
        criteria.setIsDefault(RandomUtils.flipCoin() ? RandomUtils.randomBoolean() : null);
        return criteria;
      };
    }

    private Function<AccountCriteria, AccountCriteria> withPage() {
      return criteria -> {
        PageCriteria pageCriteria = new PageCriteria();
        pageCriteria.setOffset(0);
        pageCriteria.setLimit(Integer.MAX_VALUE);
        criteria.setPage(pageCriteria);
        return criteria;
      };
    }

    private Function<AccountCriteria, AccountCriteria> withSort() {
      return criteria -> {
        criteria.setSort(
            new SortCriteria<>(AccountSortAttributeEnum.ACCOUNT_NAME, Sort.Direction.ASC));
        return criteria;
      };
    }
  }
}
