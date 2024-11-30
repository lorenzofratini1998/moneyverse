package it.moneyverse.account.services;

import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.account.utils.mapper.AccountMapper;
import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.utils.SecurityContextUtils;
import jakarta.ws.rs.ForbiddenException;
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
    if (accountRepository.existsByUsernameAndAccountName(request.username(), request.accountName())) {
      throw new ResourceAlreadyExistsException("Account with name %s already exists".formatted(request.accountName()));
    }
    LOGGER.info("Creating account {} for user {}", request.accountName(), request.username());
    Account account = AccountMapper.toAccount(request);
    if (accountRepository.findDefaultAccountByUser(request.username()).isEmpty()) {
      LOGGER.info("Setting default account for user {}", request.username());
      account.setDefault(Boolean.TRUE);
    }
    AccountDto result = AccountMapper.toAccountDto(accountRepository.save(account));
    LOGGER.info("Saved account {} for user {}", result.getAccountId(), request.username());
    return result;
  }
}
