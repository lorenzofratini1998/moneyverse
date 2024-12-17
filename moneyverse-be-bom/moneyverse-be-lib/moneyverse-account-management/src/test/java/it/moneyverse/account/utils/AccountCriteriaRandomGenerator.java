package it.moneyverse.account.utils;

import it.moneyverse.account.enums.AccountSortAttributeEnum;
import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.core.enums.AccountCategoryEnum;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.test.CriteriaRandomGenerator;
import it.moneyverse.test.model.TestContext;
import it.moneyverse.test.utils.RandomUtils;
import org.springframework.data.domain.Sort;

public class AccountCriteriaRandomGenerator extends CriteriaRandomGenerator<AccountCriteria> {

  private final AccountCriteria criteria;

  public AccountCriteriaRandomGenerator(TestContext testContext) {
    super(testContext);
    this.criteria = new AccountCriteria();
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
            testContext.getModel().getAccounts().stream()
                .map(AccountModel::getBalance)
                .toList())
            : null);
  }

  private void withRandomBalanceTarget() {
    criteria.setBalanceTarget(
        Math.random() < 0.5
            ? randomCriteriaBound(
            testContext.getModel().getAccounts().stream()
                .map(AccountModel::getBalanceTarget)
                .toList())
            : null);
  }

  private void withRandomAccountCategory() {
    criteria.setAccountCategory(
        Math.random() < 0.5 ? RandomUtils.randomEnum(AccountCategoryEnum.class) : null);
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
