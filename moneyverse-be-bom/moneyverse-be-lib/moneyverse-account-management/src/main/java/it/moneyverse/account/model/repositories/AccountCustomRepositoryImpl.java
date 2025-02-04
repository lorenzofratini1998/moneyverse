package it.moneyverse.account.model.repositories;

import it.moneyverse.account.enums.AccountSortAttributeEnum;
import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.core.model.dto.SortCriteria;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Repository;

@Repository
public class AccountCustomRepositoryImpl implements AccountCustomRepository {

  private final EntityManager em;
  private final CriteriaBuilder cb;

  public AccountCustomRepositoryImpl(EntityManager em) {
    this.em = em;
    this.cb = em.getCriteriaBuilder();
  }

  @Override
  public List<Account> findAccounts(UUID userId, AccountCriteria param) {
    CriteriaQuery<Account> cq = cb.createQuery(Account.class);
    Root<Account> root = cq.from(Account.class);
    Predicate predicate = new AccountPredicateBuilder(cb, root).build(userId, param);
    cq.where(predicate);
    if (param.getSort() != null) {
      cq.orderBy(getOrder(param.getSort(), root));
    }
    TypedQuery<Account> query = em.createQuery(cq);
    if (param.getPage() != null) {
      query.setFirstResult(param.getPage().getOffset());
      query.setMaxResults(param.getPage().getLimit());
    }
    return query.getResultList();
  }

  private Order getOrder(
      SortCriteria<AccountSortAttributeEnum> sortCriteria, Root<Account> account) {
    return sortCriteria.getDirection() == Direction.ASC
        ? cb.asc(account.get(sortCriteria.getAttribute().getField()))
        : cb.desc(account.get(sortCriteria.getAttribute().getField()));
  }
}
