package it.moneyverse.account.services;

import it.moneyverse.account.enums.AccountSortAttributeEnum;
import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.account.utils.mapper.AccountMapper;
import it.moneyverse.core.enums.SortAttribute;
import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.core.services.UserServiceClient;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
public class AccountManagementService implements AccountService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountManagementService.class);
  private final AccountRepository accountRepository;
  private final UserServiceClient userServiceClient;

  public AccountManagementService(
      AccountRepository accountRepository, UserServiceGrpcClient userServiceClient) {
    this.accountRepository = accountRepository;
    this.userServiceClient = userServiceClient;
  }

  @Override
  public AccountDto createAccount(AccountRequestDto request) {
    if (Boolean.FALSE.equals(userServiceClient.checkIfUserExists(request.username()))) {
      throw new ResourceNotFoundException("User %s does not exists".formatted(request.username()));
    }
    if (Boolean.TRUE.equals(accountRepository.existsByUsernameAndAccountName(
        request.username(), request.accountName()))) {
      throw new ResourceAlreadyExistsException(
          "Account with name %s already exists".formatted(request.accountName()));
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

  @Override
  public List<AccountDto> findAccounts(AccountCriteria criteria) {
    if (criteria.getPage() == null) {
      criteria.setPage(new PageCriteria());
    }
    if (criteria.getSort() == null) {
      criteria.setSort(
          new SortCriteria<>(
              SortAttribute.getDefault(AccountSortAttributeEnum.class), Direction.ASC));
    }
    LOGGER.info("Finding accounts with filters: {}", criteria);
    return AccountMapper.toAccountDto(accountRepository.findAccounts(criteria));
  }

  @Override
  public AccountDto findAccountByAccountId(UUID accountId) {
    Account account = accountRepository.findById(accountId).orElseThrow(() -> new ResourceNotFoundException("Account %s not found".formatted(accountId)));
    return AccountMapper.toAccountDto(account);
  }
}
