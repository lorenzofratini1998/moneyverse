package it.moneyverse.account.model.dto;

import it.moneyverse.account.enums.AccountSortAttributeEnum;
import it.moneyverse.core.enums.AccountCategoryEnum;
import it.moneyverse.core.model.dto.BoundCriteria;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import java.util.Optional;

public class AccountCriteria {

  private String username;
  private BoundCriteria balance;
  private BoundCriteria balanceTarget;
  private AccountCategoryEnum accountCategory;
  private Boolean isDefault;
  private PageCriteria page;
  private SortCriteria<AccountSortAttributeEnum> sort;

  public Optional<String> getUsername() {
    return Optional.ofNullable(username);
  }

  public void setUsername(String username) {
    this.username = username;
  }

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

  public Optional<AccountCategoryEnum> getAccountCategory() {
    return Optional.ofNullable(accountCategory);
  }

  public void setAccountCategory(AccountCategoryEnum accountCategory) {
    this.accountCategory = accountCategory;
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
}
