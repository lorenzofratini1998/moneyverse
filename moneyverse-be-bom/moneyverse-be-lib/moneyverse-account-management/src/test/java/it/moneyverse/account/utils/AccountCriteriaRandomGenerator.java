package it.moneyverse.account.utils;

import it.moneyverse.account.enums.AccountSortAttributeEnum;
import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.core.enums.AccountCategoryEnum;
import it.moneyverse.core.model.dto.BoundCriteria;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.test.model.TestContext;
import it.moneyverse.test.utils.RandomUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Sort;

class AccountCriteriaRandomGenerator {

  private final TestContext testContext;

  public AccountCriteriaRandomGenerator(TestContext testContext) {
    this.testContext = testContext;
  }

  public AccountCriteria generate() {
    AccountCriteria criteria = new AccountCriteria();
    criteria.setUsername(Math.random() < 0.5 ? testContext.getRandomUser().getUsername() : null);
    criteria.setBalance(
        Math.random() < 0.5
            ? randomCriteriaBound(
                testContext.getModel().getAccounts().stream()
                    .map(AccountModel::getBalance)
                    .toList())
            : null);
    criteria.setBalanceTarget(
        Math.random() < 0.5
            ? randomCriteriaBound(
                testContext.getModel().getAccounts().stream()
                    .map(AccountModel::getBalanceTarget)
                    .toList())
            : null);
    criteria.setAccountCategory(
        Math.random() < 0.5 ? RandomUtils.randomEnum(AccountCategoryEnum.class) : null);
    criteria.setIsDefault(Math.random() < 0.5 ? Math.random() < 0.5 : null);
    criteria.setPage(new PageCriteria());
    criteria.setSort(new SortCriteria<>(AccountSortAttributeEnum.ACCOUNT_NAME, Sort.Direction.ASC));
    return criteria;
  }

  private BoundCriteria randomCriteriaBound(List<BigDecimal> values) {
    BigDecimal minBalance = findMin(values);
    BigDecimal maxBalance = findMax(values);
    BoundCriteria criteria = new BoundCriteria();
    criteria.setLower(
        RandomUtils.randomDecimal(
                minBalance.doubleValue(),
                maxBalance.divide(BigDecimal.TWO, RoundingMode.HALF_DOWN).doubleValue())
            .setScale(2, RoundingMode.HALF_DOWN));
    criteria.setUpper(
        RandomUtils.randomDecimal(
                maxBalance.divide(BigDecimal.TWO, RoundingMode.HALF_DOWN).doubleValue(),
                maxBalance.doubleValue())
            .setScale(2, RoundingMode.HALF_DOWN));
    return criteria;
  }

  private BigDecimal findMin(List<BigDecimal> values) {
    return values.stream().filter(Objects::nonNull).min(Comparator.naturalOrder()).get();
  }

  private BigDecimal findMax(List<BigDecimal> values) {
    return values.stream().filter(Objects::nonNull).max(Comparator.naturalOrder()).get();
  }
}
