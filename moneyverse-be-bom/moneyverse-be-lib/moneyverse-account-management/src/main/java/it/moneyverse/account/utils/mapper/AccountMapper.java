package it.moneyverse.account.utils.mapper;

import it.moneyverse.account.model.dto.AccountCategoryDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.dto.AccountUpdateRequestDto;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.entities.AccountCategory;
import it.moneyverse.core.model.dto.AccountDto;
import it.moneyverse.core.model.dto.StyleDto;
import it.moneyverse.core.utils.mappers.StyleMapper;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class AccountMapper {

  private AccountMapper() {}

  public static AccountCategoryDto toAccountCategoryDto(AccountCategory accountCategory) {
    if (accountCategory == null) {
      return null;
    }
    return AccountCategoryDto.builder()
        .withAccountCategoryId(accountCategory.getAccountCategoryId())
        .withName(accountCategory.getName().toUpperCase())
        .withDescription(accountCategory.getDescription())
        .withStyle(
            StyleDto.builder()
                .withColor(accountCategory.getStyle().getColor())
                .withIcon(accountCategory.getStyle().getIcon())
                .build())
        .build();
  }

  public static Account toAccount(AccountRequestDto request, AccountCategory accountCategory) {
    if (request == null) {
      return null;
    }
    Account account = new Account();
    account.setUserId(request.userId());
    account.setAccountName(request.accountName());
    account.setBalance(request.balance() != null ? request.balance() : BigDecimal.ZERO);
    account.setBalanceTarget(request.balanceTarget());
    account.setAccountCategory(accountCategory);
    account.setAccountDescription(request.accountDescription());
    account.setCurrency(request.currency());
    account.setStyle(StyleMapper.toStyle(request.style()));
    return account;
  }

  public static AccountDto toAccountDto(Account account) {
    if (account == null) {
      return null;
    }
    return AccountDto.builder()
        .withAccountId(account.getAccountId())
        .withUserId(account.getUserId())
        .withAccountName(account.getAccountName())
        .withBalance(account.getBalance())
        .withBalanceTarget(account.getBalanceTarget())
        .withAccountCategory(account.getAccountCategory().getName().toUpperCase())
        .withAccountDescription(account.getAccountDescription())
        .withCurrency(account.getCurrency())
        .withDefault(account.isDefault())
        .withStyle(
            StyleDto.builder()
                .withColor(account.getStyle().getColor())
                .withIcon(account.getStyle().getIcon())
                .build())
        .build();
  }

  public static List<AccountDto> toAccountDto(List<Account> entities) {
    if (entities.isEmpty()) {
      return Collections.emptyList();
    }
    return entities.stream().map(AccountMapper::toAccountDto).toList();
  }

  public static Account partialUpdate(
      Account account, AccountUpdateRequestDto request, AccountCategory accountCategory) {
    if (request == null) {
      return null;
    }
    account.setAccountName(request.accountName());
    account.setBalance(request.balance());
    account.setBalanceTarget(request.balanceTarget());
    account.setAccountCategory(accountCategory);
    account.setAccountDescription(request.accountDescription());
    account.setDefault(request.isDefault());
    account.setStyle(StyleMapper.toStyle(request.style()));
    return account;
  }
}
