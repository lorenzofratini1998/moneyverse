package it.moneyverse.account.model.repositories;

import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.entities.AccountCategory_;
import it.moneyverse.account.model.entities.Account_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class AccountPredicateBuilder {

  private final CriteriaBuilder cb;
  private final Root<Account> root;
  private final List<Predicate> predicates;

  public AccountPredicateBuilder(CriteriaBuilder cb, Root<Account> root) {
    this.cb = cb;
    this.root = root;
    predicates = new ArrayList<>();
  }

  public Predicate build(AccountCriteria param) {
    withUsername(param);
    withBalance(param);
    withBalanceTarget(param);
    withAccountCategory(param);
    withIsDefault(param);
    return cb.and(predicates.toArray(new Predicate[0]));
  }

  private void withUsername(AccountCriteria param) {
    param
        .getUsername()
        .ifPresent(username -> predicates.add(cb.equal(root.get(Account_.USERNAME), username)));
  }

  private void withBalance(AccountCriteria param) {
    param
        .getBalance()
        .ifPresent(
            balance -> {
              balance
                  .getLower()
                  .ifPresent(
                      lower -> predicates.add(cb.greaterThan(root.get(Account_.BALANCE), lower)));
              balance
                  .getUpper()
                  .ifPresent(
                      upper -> predicates.add(cb.lessThan(root.get(Account_.BALANCE), upper)));
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
                          predicates.add(cb.greaterThan(root.get(Account_.BALANCE_TARGET), lower)));
              balanceTarget
                  .getUpper()
                  .ifPresent(
                      upper ->
                          predicates.add(cb.lessThan(root.get(Account_.BALANCE_TARGET), upper)));
            });
  }

  private void withAccountCategory(AccountCriteria param) {
    param
        .getAccountCategory()
        .ifPresent(
            category ->
                predicates.add(
                    cb.equal(
                        root.get(Account_.ACCOUNT_CATEGORY).get(AccountCategory_.NAME), category)));
  }

  private void withIsDefault(AccountCriteria param) {
    param
        .getIsDefault()
        .ifPresent(isDefault -> predicates.add(cb.equal(root.get(Account_.IS_DEFAULT), isDefault)));
  }
}
