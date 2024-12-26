package it.moneyverse.account.utils;

import it.moneyverse.account.enums.AccountSortAttributeEnum;
import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.test.CriteriaRandomGenerator;
import org.springframework.data.domain.Sort;

public class AccountCriteriaRandomGenerator extends CriteriaRandomGenerator<AccountCriteria> {

  private final AccountCriteria criteria;
  private final AccountTestContext testContext;

  public AccountCriteriaRandomGenerator(AccountTestContext testContext) {
    this.criteria = new AccountCriteria();
    this.testContext = testContext;
  }

  @Override
  public AccountCriteria generate() {
    withRandomUsername();
    withRandomBalance();
    withRandomBalanceTarget();
    withRandomAccountCategory();
    withRandomIsDefault();
    withPage();
    withSort();
    return criteria;
  }

  private void withRandomUsername() {
    criteria.setUsername(Math.random() < 0.5 ? testContext.getRandomUser().getUsername() : null);
  }

  private void withRandomBalance() {
    criteria.setBalance(
        Math.random() < 0.5
            ? randomCriteriaBound(
                testContext.getAccounts().stream().map(Account::getBalance).toList())
            : null);
  }

  private void withRandomBalanceTarget() {
    criteria.setBalanceTarget(
        Math.random() < 0.5
            ? randomCriteriaBound(
                testContext.getAccounts().stream().map(Account::getBalanceTarget).toList())
            : null);
  }

  private void withRandomAccountCategory() {
    criteria.setAccountCategory(
        Math.random() < 0.5 ? testContext.getRandomAccountCategory().getName() : null);
  }

  private void withRandomIsDefault() {
    criteria.setIsDefault(Math.random() < 0.5 ? Math.random() < 0.5 : null);
  }

  private void withPage() {
    criteria.setPage(new PageCriteria());
  }

  private void withSort() {
    criteria.setSort(new SortCriteria<>(AccountSortAttributeEnum.ACCOUNT_NAME, Sort.Direction.ASC));
  }
}
