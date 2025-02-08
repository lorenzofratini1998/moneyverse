package it.moneyverse.budget.model.dto;

import it.moneyverse.budget.enums.BudgetSortAttributeEnum;
import it.moneyverse.core.model.dto.BoundCriteria;
import it.moneyverse.core.model.dto.DateCriteria;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.core.utils.JsonUtils;
import java.util.Optional;

public class BudgetCriteria {

  private BoundCriteria amount;
  private BoundCriteria budgetLimit;
  private String currency;
  private DateCriteria date;
  private PageCriteria page;
  private SortCriteria<BudgetSortAttributeEnum> sort;

  public Optional<BoundCriteria> getAmount() {
    return Optional.ofNullable(amount);
  }

  public void setAmount(BoundCriteria amount) {
    this.amount = amount;
  }

  public Optional<BoundCriteria> getBudgetLimit() {
    return Optional.ofNullable(budgetLimit);
  }

  public void setBudgetLimit(BoundCriteria budgetLimit) {
    this.budgetLimit = budgetLimit;
  }

  public Optional<String> getCurrency() {
    return Optional.ofNullable(currency);
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public Optional<DateCriteria> getDate() {
    return Optional.ofNullable(date);
  }

  public void setDate(DateCriteria date) {
    this.date = date;
  }

  public PageCriteria getPage() {
    return page;
  }

  public void setPage(PageCriteria page) {
    this.page = page;
  }

  public SortCriteria<BudgetSortAttributeEnum> getSort() {
    return sort;
  }

  public void setSort(SortCriteria<BudgetSortAttributeEnum> sort) {
    this.sort = sort;
  }

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
