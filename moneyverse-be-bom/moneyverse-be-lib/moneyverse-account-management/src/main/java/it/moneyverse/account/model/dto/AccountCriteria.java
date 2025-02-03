package it.moneyverse.account.model.dto;

import it.moneyverse.account.enums.AccountSortAttributeEnum;
import it.moneyverse.core.model.dto.BoundCriteria;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.core.utils.JsonUtils;
import java.util.Optional;
import java.util.UUID;

public class AccountCriteria {

  private UUID userId;
  private BoundCriteria balance;
  private BoundCriteria balanceTarget;
  private String accountCategory;
  private String currency;
  private Boolean isDefault;
  private PageCriteria page;
  private SortCriteria<AccountSortAttributeEnum> sort;

  public Optional<UUID> getUserId() {
    return Optional.ofNullable(userId);
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
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

  public Optional<String> getAccountCategory() {
    return Optional.ofNullable(accountCategory);
  }

  public void setAccountCategory(String accountCategory) {
    this.accountCategory = accountCategory;
  }

  public Optional<String> getCurrency() {
    return Optional.ofNullable(currency);
  }

  public void setCurrency(String currency) {
    this.currency = currency;
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
