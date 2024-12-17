package it.moneyverse.budget.model.repositories;

import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.budget.model.entities.Budget_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;

public class BudgetPredicateBuilder {

    private final CriteriaBuilder cb;
    private final Root<Budget> root;
    private final List<Predicate> predicates;

    public BudgetPredicateBuilder(CriteriaBuilder cb, Root<Budget> root) {
        this.cb = cb;
        this.root = root;
        this.predicates = new ArrayList<>();
    }

    public Predicate build(BudgetCriteria param) {
        withUsername(param);
        withAmount(param);
        withBudgetLimit(param);
        return cb.and(predicates.toArray(new Predicate[0]));
    }

    private void withUsername(BudgetCriteria param) {
        param
            .getUsername()
            .ifPresent(username -> predicates.add(cb.equal(root.get(Budget_.USERNAME), username)));
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
                                .ifPresent(
                                        upper -> predicates.add(cb.lessThan(root.get(Budget_.AMOUNT), upper)));
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
                                            lower -> predicates.add(cb.greaterThan(root.get(Budget_.BUDGET_LIMIT), lower)));
                            budgetLimit
                                    .getUpper()
                                    .ifPresent(
                                            upper -> predicates.add(cb.lessThan(root.get(Budget_.BUDGET_LIMIT), upper)));
                        });
    }
}
