package it.moneyverse.account.utils.helper;

import it.moneyverse.account.AccountSortAttributeEnum;
import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.core.enums.AccountCategoryEnum;
import it.moneyverse.core.model.dto.BoundCriteria;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.core.model.entities.AccountModel;
import it.moneyverse.test.model.TestContextModel;
import it.moneyverse.test.utils.RandomUtils;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;

public class AccountCriteriaRandomGenerator {

  private final TestContextModel model;

  public AccountCriteriaRandomGenerator(TestContextModel model) {
    this.model = model;
  }

  public AccountCriteria generate() {
    AccountCriteria criteria = new AccountCriteria();
    criteria.setUsername(Math.random() < 0.5 ? model.getRandomUser().getUsername() : null);
    criteria.setBalance(
        Math.random() < 0.5
            ? randomCriteriaBound(
                model.getAccounts().stream().map(AccountModel::getBalance).toList())
            : null);
    criteria.setBalanceTarget(
        Math.random() < 0.5
            ? randomCriteriaBound(
                model.getAccounts().stream().map(AccountModel::getBalanceTarget).toList())
            : null);
    criteria.setAccountCategory(
        Math.random() < 0.5 ? RandomUtils.randomEnum(AccountCategoryEnum.class) : null);
    criteria.setDefault(Math.random() < 0.5 ? Math.random() < 0.5 : null);
    criteria.setPage(new PageCriteria());
    /*criteria.setSort(
        Math.random() < 0.5
            ? new SortCriteria<>(
                RandomUtils.randomEnum(AccountSortAttributeEnum.class), Direction.ASC)
            : null);*/
    criteria.setSort(new SortCriteria<>(RandomUtils.randomEnum(AccountSortAttributeEnum.class), Direction.ASC));
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
