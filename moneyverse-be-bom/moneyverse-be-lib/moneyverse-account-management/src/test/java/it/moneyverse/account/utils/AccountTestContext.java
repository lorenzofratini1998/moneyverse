package it.moneyverse.account.utils;

import static it.moneyverse.account.enums.AccountSortAttributeEnum.*;

import it.moneyverse.account.enums.AccountSortAttributeEnum;
import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.entities.AccountCategory;
import it.moneyverse.account.model.entities.AccountFactory;
import it.moneyverse.core.enums.SortAttribute;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.model.TestContext;
import it.moneyverse.test.model.dto.ScriptMetadata;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.services.SQLScriptService;
import it.moneyverse.test.utils.RandomUtils;
import java.nio.file.Path;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort;

public class AccountTestContext extends TestContext<AccountTestContext> {

  private static AccountTestContext currentInstance;

  private final List<AccountCategory> categories;
  private final List<Account> accounts;

  public AccountTestContext(KeycloakContainer keycloakContainer) {
    super(keycloakContainer);
    categories = AccountFactory.createAccountCategories();
    accounts = AccountFactory.createAccounts(getUsers(), categories);
    setCurrentInstance(this);
  }

  public AccountTestContext() {
    super();
    categories = AccountFactory.createAccountCategories();
    accounts = AccountFactory.createAccounts(getUsers(), categories);
    setCurrentInstance(this);
  }

  private static void setCurrentInstance(AccountTestContext instance) {
    currentInstance = instance;
  }

  protected static AccountTestContext getCurrentInstance() {
    if (currentInstance == null) {
      throw new IllegalStateException("TestContext instance is not set.");
    }
    return currentInstance;
  }

  public List<Account> getAccounts() {
    return accounts;
  }

  public List<AccountCategory> getCategories() {
    return categories;
  }

  private static AccountRequestDto toAccountRequest(Account account) {
    return new AccountRequestDto(
        account.getUserId(),
        account.getAccountName(),
        account.getBalance(),
        account.getBalanceTarget(),
        account.getAccountCategory().getName(),
        account.getAccountDescription(),
        account.getCurrency());
  }

  public Account getRandomAccount(UUID userId) {
    List<Account> userAccounts =
        accounts.stream().filter(account -> account.getUserId().equals(userId)).toList();
    return userAccounts.get(RandomUtils.randomInteger(0, userAccounts.size() - 1));
  }

  public AccountCategory getRandomAccountCategory() {
    return categories.get(RandomUtils.randomInteger(0, categories.size() - 1));
  }

  public AccountRequestDto createAccountForUser(UUID userId) {
    AccountCategory category = getRandomAccountCategory();
    return toAccountRequest(AccountFactory.fakeAccount(userId, category, accounts.size()));
  }

  public AccountDto getExpectedAccountDto(AccountRequestDto request) {
    if (getUserAccounts(request.userId()).stream().anyMatch(Account::isDefault)) {
      return toAccountDto(request, Boolean.FALSE);
    }
    return toAccountDto(request, Boolean.TRUE);
  }

  private AccountDto toAccountDto(AccountRequestDto request, Boolean isDefault) {
    return AccountDto.builder()
        .withUserId(request.userId())
        .withAccountName(request.accountName())
        .withAccountCategory(request.accountCategory())
        .withAccountDescription(request.accountDescription())
        .withBalance(request.balance())
        .withBalanceTarget(request.balanceTarget())
        .withCurrency(request.currency())
        .withDefault(isDefault)
        .build();
  }

  public AccountCriteria createAccountFilters() {
    return new AccountCriteriaRandomGenerator(getCurrentInstance()).generate();
  }

  public List<Account> filterAccounts(AccountCriteria criteria) {
    return accounts.stream()
        .filter(
            account ->
                criteria.getUserId().map(userId -> userId.equals(account.getUserId())).orElse(true))
        .filter(
            account ->
                criteria
                    .getBalance()
                    .map(
                        balanceCriteria ->
                            account.getBalance() != null
                                && filterByBound(account.getBalance(), balanceCriteria))
                    .orElse(true))
        .filter(
            account ->
                criteria
                    .getBalanceTarget()
                    .map(
                        balanceTargetCriteria ->
                            account.getBalanceTarget() != null
                                && filterByBound(account.getBalanceTarget(), balanceTargetCriteria))
                    .orElse(true))
        .filter(
            account ->
                criteria
                    .getAccountCategory()
                    .map(category -> category.equals(account.getAccountCategory().getName()))
                    .orElse(true))
        .filter(
            account ->
                criteria
                    .getCurrency()
                    .map(currency -> currency.equals(account.getCurrency()))
                    .orElse(true))
        .filter(
            account ->
                criteria
                    .getIsDefault()
                    .map(isDefault -> isDefault.equals(account.isDefault()))
                    .orElse(true))
        .sorted((a, b) -> sortByCriteria(a, b, criteria.getSort()))
        .skip(criteria.getPage().getOffset())
        .limit(criteria.getPage().getLimit())
        .toList();
  }

  private int sortByCriteria(
      Account a, Account b, SortCriteria<AccountSortAttributeEnum> sortCriteria) {
    if (sortCriteria == null) {
      return 0;
    }

    SortAttribute attribute = sortCriteria.getAttribute();
    Sort.Direction direction = sortCriteria.getDirection();

    int comparison =
        switch (attribute) {
          case ACCOUNT_NAME -> a.getAccountName().compareTo(b.getAccountName());
          case ACCOUNT_CATEGORY ->
              a.getAccountCategory().getName().compareTo(b.getAccountCategory().getName());
          case BALANCE -> a.getBalance().compareTo(b.getBalance());
          case BALANCE_TARGET -> a.getBalanceTarget().compareTo(b.getBalanceTarget());
          case USER_ID -> a.getUserId().compareTo(b.getUserId());
          default -> 0;
        };

    return direction == Sort.Direction.ASC ? comparison : -comparison;
  }

  public int getAccountsCount() {
    return accounts.size();
  }

  public List<Account> getUserAccounts(UUID userId) {
    return accounts.stream().filter(account -> userId.equals(account.getUserId())).toList();
  }

  @Override
  public AccountTestContext self() {
    return this;
  }

  @Override
  public AccountTestContext generateScript(Path dir) {
    new EntityScriptGenerator(new ScriptMetadata(dir, categories, accounts), new SQLScriptService())
        .execute();
    return self();
  }
}
