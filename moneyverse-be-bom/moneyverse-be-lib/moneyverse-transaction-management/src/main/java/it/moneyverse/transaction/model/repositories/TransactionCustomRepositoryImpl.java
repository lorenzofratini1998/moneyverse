package it.moneyverse.transaction.model.repositories;

import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.transaction.enums.TransactionSortAttributeEnum;
import it.moneyverse.transaction.model.dto.TransactionCriteria;
import it.moneyverse.transaction.model.entities.Transaction;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionCustomRepositoryImpl implements TransactionCustomRepository {

  private final EntityManager em;
  private final CriteriaBuilder cb;

  public TransactionCustomRepositoryImpl(EntityManager em) {
    this.em = em;
    this.cb = em.getCriteriaBuilder();
  }

  @Override
  public List<Transaction> findTransactions(TransactionCriteria param) {
    CriteriaQuery<Transaction> cq = cb.createQuery(Transaction.class);
    Root<Transaction> root = cq.from(Transaction.class);
    Predicate predicate = new TransactionPredicateBuilder(cb, root).build(param);
    cq.where(predicate);
    if (param.getSort() != null) {
      cq.orderBy(getOrder(param.getSort(), root));
    }
    TypedQuery<Transaction> query = em.createQuery(cq);
    if (param.getPage() != null) {
      query.setFirstResult(param.getPage().getOffset());
      query.setMaxResults(param.getPage().getLimit());
    }
    return query.getResultList();
  }

  private Order getOrder(
      SortCriteria<TransactionSortAttributeEnum> sortCriteria, Root<Transaction> account) {
    return sortCriteria.getDirection() == Sort.Direction.ASC
        ? cb.asc(account.get(sortCriteria.getAttribute().getField()))
        : cb.desc(account.get(sortCriteria.getAttribute().getField()));
  }
}
