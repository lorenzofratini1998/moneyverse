package it.moneyverse.budget.utils;

import it.moneyverse.budget.enums.BudgetSortAttributeEnum;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.core.model.entities.BudgetModel;
import it.moneyverse.test.CriteriaRandomGenerator;
import it.moneyverse.test.model.TestContext;
import it.moneyverse.budget.model.dto.BudgetCriteria;
import org.springframework.data.domain.Sort;

public class BudgetCriteriaRandomGenerator extends CriteriaRandomGenerator<BudgetCriteria> {

    private final BudgetCriteria criteria;

    public BudgetCriteriaRandomGenerator(TestContext testContext) {
        super(testContext);
        this.criteria = new BudgetCriteria();
    }

    @Override
    public BudgetCriteria generate() {
        withRandomUsername();
        withRandomAmount();
        withRandomBudgetLimit();
        withPage();
        withSort();
        return criteria;
    }

    private void withRandomUsername() {
        criteria.setUsername(Math.random() < 0.5 ? testContext.getRandomUser().getUsername() : null);
    }

    private void withRandomAmount() {
        criteria.setAmount(
                Math.random() < 0.5
                        ? randomCriteriaBound(
                        testContext.getModel().getBudgets().stream()
                                .map(BudgetModel::getAmount)
                                .toList())
                        : null
        );
    }

    private void withRandomBudgetLimit() {
        criteria.setBudgetLimit(Math.random() < 0.5
                ? randomCriteriaBound(
                testContext.getModel().getBudgets().stream()
                        .map(BudgetModel::getBudgetLimit)
                        .toList())
                : null
        );
    }

    private void withPage() {
        criteria.setPage(new PageCriteria());
    }

    private void withSort() {
        criteria.setSort(new SortCriteria<>(BudgetSortAttributeEnum.BUDGET_NAME, Sort.Direction.ASC));
    }

}
