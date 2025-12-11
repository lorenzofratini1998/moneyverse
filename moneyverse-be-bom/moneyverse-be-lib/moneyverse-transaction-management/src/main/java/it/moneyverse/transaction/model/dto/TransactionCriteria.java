package it.moneyverse.transaction.model.dto;

import it.moneyverse.core.model.dto.BoundCriteria;
import it.moneyverse.core.model.dto.DateCriteria;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.core.utils.JsonUtils;
import it.moneyverse.transaction.enums.TransactionSortAttributeEnum;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class TransactionCriteria {
  private List<UUID> accounts;
  private List<UUID> categories;
  private DateCriteria date;
  private BoundCriteria amount;
  private List<UUID> tags;
  private UUID budget;
  private Boolean isSubscription;
  private Boolean isTransfer;
  private PageCriteria page;
  private SortCriteria<TransactionSortAttributeEnum> sort;

  public Optional<List<UUID>> getAccounts() {
    return Optional.ofNullable(accounts);
  }

  public void setAccounts(List<UUID> accounts) {
    this.accounts = accounts;
  }

  public Optional<List<UUID>> getCategories() {
    return Optional.ofNullable(categories);
  }

  public void setCategories(List<UUID> categories) {
    this.categories = categories;
  }

  public Optional<DateCriteria> getDate() {
    return Optional.ofNullable(date);
  }

  public void setDate(DateCriteria date) {
    this.date = date;
  }

  public Optional<BoundCriteria> getAmount() {
    return Optional.ofNullable(amount);
  }

  public void setAmount(BoundCriteria amount) {
    this.amount = amount;
  }

  public Optional<List<UUID>> getTags() {
    return Optional.ofNullable(tags);
  }

  public void setTags(List<UUID> tags) {
    this.tags = tags;
  }

  public Optional<UUID> getBudget() {
    return Optional.ofNullable(budget);
  }

  public void setBudget(UUID budget) {
    this.budget = budget;
  }

  public Optional<Boolean> getSubscription() {
    return Optional.ofNullable(isSubscription);
  }

  public void setSubscription(Boolean subscription) {
    isSubscription = subscription;
  }

  public Optional<Boolean> getTransfer() {
    return Optional.ofNullable(isTransfer);
  }

  public void setTransfer(Boolean transfer) {
    isTransfer = transfer;
  }

  public PageCriteria getPage() {
    return page;
  }

  public void setPage(PageCriteria page) {
    this.page = page;
  }

  public SortCriteria<TransactionSortAttributeEnum> getSort() {
    return sort;
  }

  public void setSort(SortCriteria<TransactionSortAttributeEnum> sort) {
    this.sort = sort;
  }

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
