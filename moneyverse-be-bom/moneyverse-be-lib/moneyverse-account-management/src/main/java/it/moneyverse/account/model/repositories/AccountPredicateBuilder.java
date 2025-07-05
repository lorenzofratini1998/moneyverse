package it.moneyverse.account.model.repositories;

import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.entities.AccountCategory_;
import it.moneyverse.account.model.entities.Account_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class AccountPredicateBuilder {

  private final CriteriaBuilder cb;
  private final Root<Account> root;
  private final List<Predicate> predicates;

  public AccountPredicateBuilder(CriteriaBuilder cb, Root<Account> root) {
    this.cb = cb;
    this.root = root;
    predicates = new ArrayList<>();
  }

  public Predicate build(UUID userId, AccountCriteria param) {
    predicates.add(cb.equal(root.get(Account_.USER_ID), userId));
    withBalance(param);
    withBalanceTarget(param);
    withAccountCategories(param);
    withCurrencies(param);
    withIsDefault(param);
    return cb.and(predicates.toArray(new Predicate[0]));
  }

  private void withBalance(AccountCriteria param) {
    param
        .getBalance()
        .ifPresent(
            balance -> {
              balance
                  .getLower()
                  .ifPresent(
                      lower ->
                          predicates.add(
                              cb.greaterThanOrEqualTo(root.get(Account_.BALANCE), lower)));
              balance
                  .getUpper()
                  .ifPresent(
                      upper ->
                          predicates.add(cb.lessThanOrEqualTo(root.get(Account_.BALANCE), upper)));
            });
  }

  private void withBalanceTarget(AccountCriteria param) {
    param
        .getBalanceTarget()
        .ifPresent(
            balanceTarget -> {
              balanceTarget
                  .getLower()
                  .ifPresent(
                      lower ->
                          predicates.add(
                              cb.greaterThanOrEqualTo(root.get(Account_.BALANCE_TARGET), lower)));
              balanceTarget
                  .getUpper()
                  .ifPresent(
                      upper ->
                          predicates.add(
                              cb.lessThanOrEqualTo(root.get(Account_.BALANCE_TARGET), upper)));
            });
  }

  private void withAccountCategories(AccountCriteria param) {
    List<String> categories = param.getAccountCategories().orElse(Collections.emptyList());
    if (!categories.isEmpty()) {
      Predicate[] categoryPredicates =
          categories.stream()
              .map(
                  category ->
                      cb.equal(
                          root.get(Account_.ACCOUNT_CATEGORY).get(AccountCategory_.NAME), category))
              .toArray(Predicate[]::new);

      predicates.add(cb.or(categoryPredicates));
    }
  }

  private void withCurrencies(AccountCriteria param) {
    List<String> currencies = param.getCurrencies().orElse(Collections.emptyList());
    if (!currencies.isEmpty()) {
      Predicate[] currencyPredicates =
          currencies.stream()
              .map(currency -> cb.equal(root.get(Account_.CURRENCY), currency))
              .toArray(Predicate[]::new);

      predicates.add(cb.or(currencyPredicates));
    }
  }

  private void withIsDefault(AccountCriteria param) {
    param
        .getIsDefault()
        .ifPresent(isDefault -> predicates.add(cb.equal(root.get(Account_.IS_DEFAULT), isDefault)));
  }
}
