package it.moneyverse.budget.model.repositories;

import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.entities.Budget_;
import it.moneyverse.budget.model.entities.Category_;
import jakarta.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BudgetPredicateBuilder {

  private final CriteriaBuilder cb;
  private final Root<Budget> root;
  private final List<Predicate> predicates;

  public BudgetPredicateBuilder(CriteriaBuilder cb, Root<Budget> root) {
    this.cb = cb;
    this.root = root;
    this.predicates = new ArrayList<>();
  }

  public Predicate build(UUID userId, BudgetCriteria param) {
    predicates.add(cb.equal(root.join(Budget_.CATEGORY).get(Category_.USER_ID), userId));
    withAmount(param);
    withBudgetLimit(param);
    withCurrency(param);
    withDate(param);
    return cb.and(predicates.toArray(new Predicate[0]));
  }

  private void withAmount(BudgetCriteria param) {
    param
        .getAmount()
        .ifPresent(
            balance -> {
              balance
                  .getLower()
                  .ifPresent(
                      lower -> predicates.add(cb.greaterThan(root.get(Budget_.AMOUNT), lower)));
              balance
                  .getUpper()
                  .ifPresent(upper -> predicates.add(cb.lessThan(root.get(Budget_.AMOUNT), upper)));
            });
  }

  private void withBudgetLimit(BudgetCriteria param) {
    param
        .getBudgetLimit()
        .ifPresent(
            budgetLimit -> {
              budgetLimit
                  .getLower()
                  .ifPresent(
                      lower ->
                          predicates.add(cb.greaterThan(root.get(Budget_.BUDGET_LIMIT), lower)));
              budgetLimit
                  .getUpper()
                  .ifPresent(
                      upper -> predicates.add(cb.lessThan(root.get(Budget_.BUDGET_LIMIT), upper)));
            });
  }

  private void withCurrency(BudgetCriteria param) {
    param
        .getCurrency()
        .ifPresent(currency -> predicates.add(cb.equal(root.get(Budget_.CURRENCY), currency)));
  }

  private void withDate(BudgetCriteria param) {
    param
        .getDate()
        .ifPresent(
            date -> {
              date.getStart()
                  .ifPresent(
                      min ->
                          predicates.add(
                              cb.greaterThanOrEqualTo(root.get(Budget_.START_DATE), min)));
              date.getEnd()
                  .ifPresent(
                      max -> predicates.add(cb.lessThanOrEqualTo(root.get(Budget_.END_DATE), max)));
            });
  }
}
