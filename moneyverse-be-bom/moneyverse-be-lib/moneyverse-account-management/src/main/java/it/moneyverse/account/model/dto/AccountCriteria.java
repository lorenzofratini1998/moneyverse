package it.moneyverse.account.model.dto;

import it.moneyverse.account.enums.AccountSortAttributeEnum;
import it.moneyverse.core.model.dto.BoundCriteria;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.core.utils.JsonUtils;
import java.util.List;
import java.util.Optional;

public class AccountCriteria {
  
  private BoundCriteria balance;
  private BoundCriteria balanceTarget;
  private List<String> accountCategories;
  private List<String> currencies;
  private Boolean isDefault;
  private PageCriteria page;
  private SortCriteria<AccountSortAttributeEnum> sort;

  public Optional<BoundCriteria> getBalance() {
    return Optional.ofNullable(balance);
  }

  public void setBalance(BoundCriteria balance) {
    this.balance = balance;
  }

  public Optional<BoundCriteria> getBalanceTarget() {
    return Optional.ofNullable(balanceTarget);
  }

  public void setBalanceTarget(BoundCriteria balanceTarget) {
    this.balanceTarget = balanceTarget;
  }

  public Optional<List<String>> getAccountCategories() {
    return Optional.ofNullable(accountCategories);
  }

  public void setAccountCategories(List<String> accountCategories) {
    this.accountCategories = accountCategories;
  }

  public Optional<List<String>> getCurrencies() {
    return Optional.ofNullable(currencies);
  }

  public void setCurrencies(List<String> currencies) {
    this.currencies = currencies;
  }

  public Optional<Boolean> getIsDefault() {
    return Optional.ofNullable(isDefault);
  }

  public void setIsDefault(Boolean aDefault) {
    isDefault = aDefault;
  }

  public PageCriteria getPage() {
    return page;
  }

  public void setPage(PageCriteria page) {
    this.page = page;
  }

  public SortCriteria<AccountSortAttributeEnum> getSort() {
    return sort;
  }

  public void setSort(SortCriteria<AccountSortAttributeEnum> sort) {
    this.sort = sort;
  }

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
