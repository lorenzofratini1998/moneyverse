package it.moneyverse.account.model;

import static it.moneyverse.account.enums.AccountSortAttributeEnum.*;

import it.moneyverse.account.enums.AccountSortAttributeEnum;
import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.entities.AccountCategory;
import it.moneyverse.core.model.dto.BoundCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.test.extensions.testcontainers.KeycloakContainer;
import it.moneyverse.test.model.TestContext;
import it.moneyverse.test.model.dto.ScriptMetadata;
import it.moneyverse.test.operations.mapping.EntityScriptGenerator;
import it.moneyverse.test.services.SQLScriptService;
import it.moneyverse.test.utils.RandomUtils;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;
import org.springframework.data.domain.Sort;

public class AccountTestContext extends TestContext<AccountTestContext> {

  private static AccountTestContext currentInstance;

  private final List<AccountCategory> categories = AccountTestFactory.createAccountCategories();
  private final List<Account> accounts = AccountTestFactory.createAccounts(getUsers(), categories);

  public AccountTestContext(KeycloakContainer keycloakContainer) {
    super(keycloakContainer);
    init();
  }

  public AccountTestContext() {
    super();
    init();
  }

  private void init() {
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
    return AccountTestFactory.AccountRequestDtoBuilder.builder()
        .withUserId(userId)
        .withAccountCategory(category.getName())
        .build();
  }

  public AccountCriteria createAccountFilters() {
    return AccountTestFactory.AccountCriteriaBuilder.generator(getCurrentInstance()).generate();
  }

  public List<Account> filterAccounts(UUID userId, AccountCriteria criteria) {
    return accounts.stream()
        .filter(byUserId(userId))
        .filter(byBalance(criteria.getBalance()))
        .filter(byBalanceTarget(criteria.getBalanceTarget()))
        .filter(byAccountCategory(criteria.getAccountCategory()))
        .filter(byCurrency(criteria.getCurrency()))
        .filter(byDefault(criteria.getIsDefault()))
        .sorted(sortByCriteria(criteria.getSort()))
        .skip(criteria.getPage().getOffset())
        .limit(criteria.getPage().getLimit())
        .toList();
  }

  private Predicate<Account> byUserId(UUID userId) {
    return account -> userId.equals(account.getUserId());
  }

  private Predicate<Account> byBalance(Optional<BoundCriteria> balance) {
    return account ->
        balance
            .map(
                balanceCriteria ->
                    account.getBalance() != null
                        && filterByBound(account.getBalance(), balanceCriteria))
            .orElse(true);
  }

  private Predicate<Account> byBalanceTarget(Optional<BoundCriteria> balanceTarget) {
    return account ->
        balanceTarget
            .map(
                balanceTargetCriteria ->
                    account.getBalanceTarget() != null
                        && filterByBound(account.getBalanceTarget(), balanceTargetCriteria))
            .orElse(true);
  }

  private Predicate<Account> byAccountCategory(Optional<String> accountCategory) {
    return account ->
        accountCategory
            .map(category -> category.equals(account.getAccountCategory().getName()))
            .orElse(true);
  }

  private Predicate<Account> byCurrency(Optional<String> currency) {
    return account -> currency.map(curr -> curr.equals(account.getCurrency())).orElse(true);
  }

  private Predicate<Account> byDefault(Optional<Boolean> isDefault) {
    return account ->
        isDefault.map(defaultValue -> defaultValue.equals(account.isDefault())).orElse(true);
  }

  private Comparator<Account> sortByCriteria(SortCriteria<AccountSortAttributeEnum> sortCriteria) {
    if (sortCriteria == null) {
      return (a, b) -> 0;
    }

    Comparator<Account> comparator =
        switch (sortCriteria.getAttribute()) {
          case ACCOUNT_NAME -> Comparator.comparing(Account::getAccountName);
          case ACCOUNT_CATEGORY ->
              Comparator.comparing(account -> account.getAccountCategory().getName());
          case BALANCE -> Comparator.comparing(Account::getBalance);
          case BALANCE_TARGET -> Comparator.comparing(Account::getBalanceTarget);
          default -> (a, b) -> 0;
        };

    return sortCriteria.getDirection() == Sort.Direction.ASC ? comparator : comparator.reversed();
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
    EntityScriptGenerator scriptGenerator =
        new EntityScriptGenerator(
            new ScriptMetadata(dir, categories, accounts), new SQLScriptService());
    StringBuilder script = scriptGenerator.generateScript();
    scriptGenerator.save(script);
    return self();
  }
}
