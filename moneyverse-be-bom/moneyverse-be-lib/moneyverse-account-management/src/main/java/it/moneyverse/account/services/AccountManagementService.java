package it.moneyverse.account.services;

import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.model.entities.Account;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.account.utils.mapper.AccountMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AccountManagementService implements AccountService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountManagementService.class);
  private final AccountRepository accountRepository;

  public AccountManagementService(AccountRepository accountRepository) {
    this.accountRepository = accountRepository;
  }

  @Override
  public AccountDto createAccount(AccountRequestDto request) {
    LOGGER.info("Creating account {} for user {}", request.accountName(), request.userId());
    Account account = AccountMapper.toAccount(request);
    Optional<Account> defaultAccount = accountRepository.findDefaultAccount(request.userId());
    if (defaultAccount.isEmpty()) {
      LOGGER.info("Setting default account for user {}", request.userId());
      account.setDefault(Boolean.TRUE);
    }
    AccountDto result = AccountMapper.toAccountDto(accountRepository.save(account));
    LOGGER.info("Saved account {} for user {}", result.getAccountId(), request.userId());
    return result;
  }
}
