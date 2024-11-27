package it.moneyverse.account.utils.helper;

import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.entities.Account;

public class AccountTestHelper {

    public static AccountRequestDto toAccountRequest(Account account) {
        return new AccountRequestDto(
            account.getUserId(),
            account.getAccountName(),
            account.getBalance(),
            account.getBalanceTarget(),
            account.getAccountCategory(),
            account.getAccountDescription(),
            account.isDefault()
        );
    }

    private AccountTestHelper() {}
}
