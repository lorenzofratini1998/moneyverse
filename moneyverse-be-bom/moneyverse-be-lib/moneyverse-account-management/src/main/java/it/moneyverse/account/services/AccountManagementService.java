package it.moneyverse.account.services;

import it.moneyverse.account.enums.AccountSortAttributeEnum;
import it.moneyverse.account.model.dto.AccountCriteria;
import it.moneyverse.account.model.dto.AccountDto;
import it.moneyverse.account.model.dto.AccountRequestDto;
import it.moneyverse.account.model.dto.AccountUpdateRequestDto;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.event.AccountDeletionEvent;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.account.utils.mapper.AccountMapper;
import it.moneyverse.core.enums.SortAttribute;
import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.beans.AccountDeletionTopic;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.core.services.MessageProducer;
import it.moneyverse.core.services.UserServiceClient;
import java.util.List;
import java.util.UUID;

import it.moneyverse.core.services.UserServiceGrpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountManagementService implements AccountService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountManagementService.class);
  private final AccountRepository accountRepository;
  private final UserServiceClient userServiceClient;
  private final MessageProducer<UUID, String> messageProducer;

  public AccountManagementService(
      AccountRepository accountRepository,
      UserServiceGrpcClient userServiceClient,
      MessageProducer<UUID, String> messageProducer
  ) {
    this.accountRepository = accountRepository;
    this.userServiceClient = userServiceClient;
    this.messageProducer = messageProducer;
  }

  @Override
  @Transactional
  public AccountDto createAccount(AccountRequestDto request) {
    checkIfUserExists(request.username());
    checkIfAccountAlreadyExists(request.username(), request.accountName());
    LOGGER.info("Creating account {} for user {}", request.accountName(), request.username());
    Account account = AccountMapper.toAccount(request);
    if (accountRepository.findDefaultAccountsByUser(request.username()).isEmpty()) {
      LOGGER.info("Setting default account for user {}", request.username());
      account.setDefault(Boolean.TRUE);
    } else {
      account.setDefault(Boolean.FALSE);
    }
    AccountDto result = AccountMapper.toAccountDto(accountRepository.save(account));
    LOGGER.info("Saved account {} for user {}", result.getAccountId(), request.username());
    return result;
  }

  private void checkIfUserExists(String username) {
    if (Boolean.FALSE.equals(userServiceClient.checkIfUserExists(username))) {
      throw new ResourceNotFoundException("User %s does not exists".formatted(username));
    }
  }

  private void checkIfAccountAlreadyExists(String username, String accountName) {
    if (Boolean.TRUE.equals(
        accountRepository.existsByUsernameAndAccountName(username, accountName))) {
      throw new ResourceAlreadyExistsException(
          "Account with name %s already exists".formatted(accountName));
    }
  }

  @Override
  @Transactional(readOnly = true)
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
  @Transactional(readOnly = true)
  public AccountDto findAccountByAccountId(UUID accountId) {
    Account account = findAccountById(accountId);
    return AccountMapper.toAccountDto(account);
  }

  @Override
  @Transactional
  public AccountDto updateAccount(UUID accountId, AccountUpdateRequestDto request) {
    Account account = findAccountById(accountId);
    account = AccountMapper.partialUpdate(account, request);
    if (Boolean.TRUE.equals(request.isDefault())) {
      accountRepository
          .findDefaultAccountsByUser(account.getUsername())
          .stream()
          .filter(defaultAcc -> !defaultAcc.getAccountId().equals(accountId))
          .forEach(
              defaultAcc -> {
                defaultAcc.setDefault(false);
                accountRepository.save(defaultAcc);
              });
    }
    AccountDto result = AccountMapper.toAccountDto(accountRepository.save(account));
    LOGGER.info("Updated account {} for user {}", result.getAccountId(), account.getUsername());
    return result;
  }

  @Override
  @Transactional
  public void deleteAccount(UUID accountId) {
    Account account = findAccountById(accountId);
    accountRepository.delete(account);
    messageProducer.send(
        new AccountDeletionEvent(accountId, account.getUsername()), AccountDeletionTopic.TOPIC);
    LOGGER.info("Deleted account {} for user {}", account.getUsername(), account.getUsername());
  }

  @Transactional
  @Override
  public void deleteAccountsByUsername(String username) {
    LOGGER.info("Deleting accounts by username {}", username);
    accountRepository.deleteAll(accountRepository.findAccountByUsername(username));
  }

  private Account findAccountById(UUID accountId) {
    return accountRepository
        .findById(accountId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Account %s not found".formatted(accountId)));
  }
}
