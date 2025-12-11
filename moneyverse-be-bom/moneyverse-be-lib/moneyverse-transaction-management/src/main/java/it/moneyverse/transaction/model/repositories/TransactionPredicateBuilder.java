package it.moneyverse.transaction.model.repositories;

import it.moneyverse.transaction.model.dto.TransactionCriteria;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.entities.Tag_;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.entities.Transaction_;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TransactionPredicateBuilder {

  private final CriteriaBuilder cb;
  private final Root<Transaction> root;
  private final List<Predicate> predicates;

  public TransactionPredicateBuilder(CriteriaBuilder cb, Root<Transaction> root) {
    this.cb = cb;
    this.root = root;
    this.predicates = new ArrayList<>();
  }

  public Predicate build(UUID userId, TransactionCriteria param) {
    predicates.add(cb.equal(root.get(Transaction_.USER_ID), userId));
    withAccounts(param);
    withCategories(param);
    withDate(param);
    withAmount(param);
    withTags(param);
    withBudget(param);
    withIsSubscription(param);
    withIsTransfer(param);
    return cb.and(predicates.toArray(new Predicate[0]));
  }

  private void withAccounts(TransactionCriteria criteria) {
    criteria
        .getAccounts()
        .ifPresent(
            accounts -> {
              Predicate[] accountPredicates =
                  accounts.stream()
                      .map(account -> cb.equal(root.get(Transaction_.ACCOUNT_ID), account))
                      .toArray(Predicate[]::new);
              predicates.add(cb.or(accountPredicates));
            });
  }

  private void withCategories(TransactionCriteria param) {
    param
        .getCategories()
        .ifPresent(
            categories -> {
              Predicate[] categoryPredicates =
                  categories.stream()
                      .map(category -> cb.equal(root.get(Transaction_.CATEGORY_ID), category))
                      .toArray(Predicate[]::new);
              predicates.add(cb.or(categoryPredicates));
            });
  }

  private void withDate(TransactionCriteria param) {
    param
        .getDate()
        .ifPresent(
            date -> {
              date.getStart()
                  .ifPresent(
                      min ->
                          predicates.add(
                              cb.greaterThanOrEqualTo(root.get(Transaction_.DATE), min)));
              date.getEnd()
                  .ifPresent(
                      max ->
                          predicates.add(cb.lessThanOrEqualTo(root.get(Transaction_.DATE), max)));
            });
  }

  private void withAmount(TransactionCriteria param) {
    param
        .getAmount()
        .ifPresent(
            amount -> {
              amount
                  .getLower()
                  .ifPresent(
                      lower ->
                          predicates.add(
                              cb.greaterThanOrEqualTo(root.get(Transaction_.AMOUNT), lower)));
              amount
                  .getUpper()
                  .ifPresent(
                      upper ->
                          predicates.add(
                              cb.lessThanOrEqualTo(root.get(Transaction_.AMOUNT), upper)));
            });
  }

  private void withTags(TransactionCriteria criteria) {
    criteria
        .getTags()
        .ifPresent(
            tags -> {
              Join<Transaction, Tag> tagsJoin = root.join(Transaction_.TAGS);
              Predicate[] tagPredicates =
                  tags.stream()
                      .map(tag -> cb.equal(tagsJoin.get(Tag_.TAG_ID), tag))
                      .toArray(Predicate[]::new);
              predicates.add(cb.or(tagPredicates));
            });
  }

  private void withBudget(TransactionCriteria criteria) {
    criteria
        .getBudget()
        .ifPresent(budget -> predicates.add(cb.equal(root.get(Transaction_.BUDGET_ID), budget)));
  }

  private void withIsSubscription(TransactionCriteria criteria) {
    criteria
        .getSubscription()
        .ifPresent(
            subscription -> predicates.add(cb.isNotNull(root.get(Transaction_.SUBSCRIPTION))));
  }

  private void withIsTransfer(TransactionCriteria criteria) {
    criteria
        .getTransfer()
        .ifPresent(transfer -> predicates.add(cb.isNotNull(root.get(Transaction_.TRANSFER))));
  }
}
