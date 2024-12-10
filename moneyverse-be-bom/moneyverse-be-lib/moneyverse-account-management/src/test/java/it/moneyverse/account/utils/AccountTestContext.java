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
import it.moneyverse.test.utils.helper.MapperTestHelper;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Sort;

public class AccountTestContext extends TestContext {

  protected AccountTestContext(Builder builder) {
    super(builder);
  }

  public static Builder builder() {
    return new Builder();
  }

  private static AccountRequestDto toAccountRequest(Account account) {
    return new AccountRequestDto(
        account.getUsername(),
        account.getAccountName(),
        account.getBalance(),
        account.getBalanceTarget(),
        account.getAccountCategory(),
        account.getAccountDescription());
  }

  public AccountRequestDto createAccountForUser(String username) {
    return toAccountRequest(
        MapperTestHelper.map(new FakeAccount(username, model.getAccounts().size()), Account.class));
  }

  public AccountDto getExpectedAccountDto(AccountRequestDto request) {
    if (getUserAccounts(request.username()).stream().anyMatch(AccountModel::isDefault)) {
      return toAccountDto(request, Boolean.FALSE);
    }
    return toAccountDto(request, Boolean.TRUE);
  }

  private AccountDto toAccountDto(AccountRequestDto request, Boolean isDefault) {
    return AccountDto.builder()
        .withUsername(request.username())
        .withAccountName(request.accountName())
        .withAccountCategory(request.accountCategory())
        .withAccountDescription(request.accountDescription())
        .withBalance(request.balance())
        .withBalanceTarget(request.balanceTarget())
        .withDefault(isDefault)
        .build();
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

  public List<AccountModel> getUserAccounts(String username) {
    return model.getAccounts().stream()
        .filter(account -> username.equals(account.getUsername()))
        .toList();
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
