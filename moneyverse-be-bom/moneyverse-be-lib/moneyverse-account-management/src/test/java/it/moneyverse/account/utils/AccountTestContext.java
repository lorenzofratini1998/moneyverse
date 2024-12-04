package it.moneyverse.account.utils;

import static it.moneyverse.account.enums.AccountSortAttributeEnum.*;

import it.moneyverse.account.enums.AccountSortAttributeEnum;
import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.core.enums.SortAttribute;
import it.moneyverse.core.model.dto.BoundCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.test.model.TestContext;
import it.moneyverse.test.model.entities.FakeAccount;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.test.utils.helper.MapperTestHelper;
import java.math.BigDecimal;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import org.springframework.data.domain.Sort;

public class AccountTestContext extends TestContext {

  protected AccountTestContext(Builder builder) {
    super(builder);
  }

  public static Builder builder() {
    return new Builder();
  }

  public static Stream<Function<String, AccountRequestDto>> invalidAccountRequestProvider() {
    return Stream.of(
        AccountTestContext::createAccountWithNullUsername,
        AccountTestContext::createAccountWithNullAccountName,
        AccountTestContext::createAccountWithNullAccountCategory,
        AccountTestContext::createAccountWithExceedUsername);
  }

  public static AccountRequestDto createAccountWithNullUsername(String username) {
    Account account =
        MapperTestHelper.map(
            new FakeAccount(username, getCurrentInstance().getModel().getAccounts().size()),
            Account.class);
    account.setUsername(null);
    return toAccountRequest(account);
  }

  public static AccountRequestDto createAccountWithNullAccountName(String username) {
    Account account =
        MapperTestHelper.map(
            new FakeAccount(username, getCurrentInstance().getModel().getAccounts().size()),
            Account.class);
    account.setAccountName(null);
    return toAccountRequest(account);
  }

  public static AccountRequestDto createAccountWithNullAccountCategory(String username) {
    Account account =
        MapperTestHelper.map(
            new FakeAccount(username, getCurrentInstance().getModel().getAccounts().size()),
            Account.class);
    account.setAccountCategory(null);
    return toAccountRequest(account);
  }

  public static AccountRequestDto createAccountWithExceedUsername(String username) {
    Account account =
        MapperTestHelper.map(
            new FakeAccount(username, getCurrentInstance().getModel().getAccounts().size()),
            Account.class);
    account.setUsername(RandomUtils.randomString(100));
    return toAccountRequest(account);
  }

  private static AccountRequestDto toAccountRequest(Account account) {
    return new AccountRequestDto(
        account.getUsername(),
        account.getAccountName(),
        account.getBalance(),
        account.getBalanceTarget(),
        account.getAccountCategory(),
        account.getAccountDescription(),
        account.isDefault());
  }

  public AccountRequestDto createAccountForUser(String username) {
    return toAccountRequest(
        MapperTestHelper.map(new FakeAccount(username, model.getAccounts().size()), Account.class));
  }

  public AccountDto toAccountDto(AccountRequestDto request) {
    return AccountDto.builder()
        .withUsername(request.username())
        .withAccountName(request.accountName())
        .withAccountCategory(request.accountCategory())
        .withAccountDescription(request.accountDescription())
        .withBalance(request.balance())
        .withBalanceTarget(request.balanceTarget())
        .withDefault(request.isDefault())
        .build();
  }

  public AccountRequestDto createExistentAccountForUser(String username) {
    AccountModel randomExistingAccount = getRandomAccount(username);
    Account account =
        MapperTestHelper.map(new FakeAccount(username, model.getAccounts().size()), Account.class);
    account.setAccountName(randomExistingAccount.getAccountName());
    return toAccountRequest(account);
  }

  public AccountCriteria createAccountFilters() {
    return new AccountCriteriaRandomGenerator(getCurrentInstance()).generate();
  }

  public List<AccountModel> filterAccounts(AccountCriteria criteria) {
    return model.getAccounts().stream()
        .filter(
            account ->
                criteria
                    .getUsername()
                    .map(username -> username.equals(account.getUsername()))
                    .orElse(true))
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
                    .map(category -> category.equals(account.getAccountCategory()))
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

  private boolean filterByBound(BigDecimal value, BoundCriteria boundCriteria) {
    return boundCriteria.getLower().map(lower -> value.compareTo(lower) >= 0).orElse(true)
        && boundCriteria.getUpper().map(upper -> value.compareTo(upper) <= 0).orElse(true);
  }

  private int sortByCriteria(
      AccountModel a, AccountModel b, SortCriteria<AccountSortAttributeEnum> sortCriteria) {
    if (sortCriteria == null) {
      return 0;
    }

    SortAttribute attribute = sortCriteria.getAttribute();
    Sort.Direction direction = sortCriteria.getDirection();

    int comparison =
        switch (attribute) {
          case ACCOUNT_NAME -> a.getAccountName().compareTo(b.getAccountName());
          case ACCOUNT_CATEGORY -> a.getAccountCategory().compareTo(b.getAccountCategory());
          case BALANCE -> a.getBalance().compareTo(b.getBalance());
          case BALANCE_TARGET -> a.getBalanceTarget().compareTo(b.getBalanceTarget());
          case USERNAME -> a.getUsername().compareTo(b.getUsername());
          default -> 0;
        };

    return direction == Sort.Direction.ASC ? comparison : -comparison;
  }

  public int getAccountsCount() {
    return model.getAccounts().size();
  }

  public static class Builder extends TestContext.Builder<Builder> {

    @Override
    protected Builder self() {
      return this;
    }

    @Override
    public AccountTestContext build() {
      return new AccountTestContext(this);
    }
  }
}
