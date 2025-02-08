package it.moneyverse.transaction.utils;

import it.moneyverse.core.model.dto.DateCriteria;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.test.CriteriaRandomGenerator;
import it.moneyverse.test.utils.FakeUtils;
import it.moneyverse.test.utils.RandomUtils;
import it.moneyverse.transaction.enums.TransactionSortAttributeEnum;
import it.moneyverse.transaction.model.dto.TransactionCriteria;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.entities.Transaction;
import java.time.LocalDate;
import java.util.*;
import org.springframework.data.domain.Sort;

public class TransactionCriteriaRandomGenerator
    extends CriteriaRandomGenerator<TransactionCriteria> {

  private final TransactionCriteria criteria;
  private final TransactionTestContext testContext;
  private final UUID userId;

  public TransactionCriteriaRandomGenerator(UUID userId, TransactionTestContext testContext) {
    this.criteria = new TransactionCriteria();
    this.testContext = testContext;
    this.userId = userId;
  }

  @Override
  public TransactionCriteria generate() {
    withRandomAccounts();
    withRandomBudgets();
    withRandomDate();
    withRandomAmount();
    withRandomTags();
    withPage();
    withSort();
    return criteria;
  }

  private void withRandomAccounts() {
    if (Math.random() < 0.5) {
      List<UUID> accounts =
          testContext.getTransactions().stream()
              .filter(transaction -> transaction.getUserId().equals(userId))
              .map(Transaction::getAccountId)
              .toList();
      List<UUID> randomAccounts = new ArrayList<>();
      for (int i = 0;
          i
              < RandomUtils.randomInteger(
                  FakeUtils.MIN_ACCOUNTS_PER_USER, FakeUtils.MAX_ACCOUNTS_PER_USER);
          i++) {
        randomAccounts.add(accounts.get(RandomUtils.randomInteger(0, accounts.size() - 1)));
      }
      criteria.setAccounts(randomAccounts);
    }
  }

  private void withRandomBudgets() {
    if (Math.random() < 0.5) {
      List<UUID> budgets =
          testContext.getTransactions().stream()
              .filter(transaction -> transaction.getUserId().equals(userId))
              .map(Transaction::getBudgetId)
              .toList();
      List<UUID> randomBudgets = new ArrayList<>();
      for (int i = 0;
          i
              < RandomUtils.randomInteger(
                  FakeUtils.MIN_CATEGORIES_PER_USER, FakeUtils.MAX_CATEGORIES_PER_USER);
          i++) {
        randomBudgets.add(budgets.get(RandomUtils.randomInteger(0, budgets.size() - 1)));
      }
      criteria.setBudgets(randomBudgets);
    }
  }

  private void withRandomDate() {
    criteria.setDate(Math.random() < 0.5 ? randomDateCriteria() : null);
  }

  private void withRandomAmount() {
    criteria.setAmount(
        Math.random() < 0.5
            ? randomCriteriaBound(
                testContext.getTransactions().stream().map(Transaction::getAmount).toList())
            : null);
  }

  private void withRandomTags() {
    if (Math.random() < 0.5) {
      List<UUID> tags =
          testContext.getTransactions().stream()
              .filter(transaction -> transaction.getUserId().equals(userId))
              .flatMap(t -> t.getTags().stream().map(Tag::getTagId))
              .toList();
      if (tags.isEmpty()) {
        return;
      }
      List<UUID> randomTags = new ArrayList<>();
      for (int i = 0;
          i < RandomUtils.randomInteger(FakeUtils.MIN_TAGS_PER_USER, FakeUtils.MAX_TAGS_PER_USER);
          i++) {
        randomTags.add(tags.get(RandomUtils.randomInteger(0, tags.size() - 1)));
      }
      criteria.setTags(randomTags);
    }
  }

  private DateCriteria randomDateCriteria() {
    DateCriteria dateCriteria = new DateCriteria();
    LocalDate start = RandomUtils.randomLocalDate(2024, 2024);
    dateCriteria.setStart(start);
    dateCriteria.setEnd(start.plusMonths(RandomUtils.randomInteger(1, 12)));
    return dateCriteria;
  }

  private void withPage() {
    criteria.setPage(new PageCriteria());
  }

  private void withSort() {
    criteria.setSort(new SortCriteria<>(TransactionSortAttributeEnum.DATE, Sort.Direction.DESC));
  }
}
