package it.moneyverse.budget.model.repositories;

import it.moneyverse.budget.enums.BudgetSortAttributeEnum;
import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.entities.Budget;
import it.moneyverse.core.model.dto.SortCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BudgetCustomRepositoryImpl implements BudgetCustomRepository {

    private final EntityManager em;
    private final CriteriaBuilder cb;

    public BudgetCustomRepositoryImpl(EntityManager em) {
        this.em = em;
        this.cb = em.getCriteriaBuilder();
    }

    @Override
    public List<Budget> findBudgets(BudgetCriteria param) {
        CriteriaQuery<Budget> cq = cb.createQuery(Budget.class);
        Root<Budget> root = cq.from(Budget.class);
        Predicate predicate = new BudgetPredicateBuilder(cb, root).build(param);
        cq.where(predicate);
        if (param.getSort() != null) {
            cq.orderBy(getOrder(param.getSort(), root));
        }
        TypedQuery<Budget> query = em.createQuery(cq);
        if (param.getPage() != null) {
            query.setFirstResult(param.getPage().getOffset());
            query.setMaxResults(param.getPage().getLimit());
        }
        return query.getResultList();
    }

    private Order getOrder(
            SortCriteria<BudgetSortAttributeEnum> sortCriteria, Root<Budget> account) {
        return sortCriteria.getDirection() == Sort.Direction.ASC
                ? cb.asc(account.get(sortCriteria.getAttribute().getField()))
                : cb.desc(account.get(sortCriteria.getAttribute().getField()));
    }
}
