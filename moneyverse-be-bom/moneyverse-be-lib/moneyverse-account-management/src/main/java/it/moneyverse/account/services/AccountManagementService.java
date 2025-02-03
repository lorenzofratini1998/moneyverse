package it.moneyverse.account.services;

import it.moneyverse.account.enums.AccountSortAttributeEnum;
import it.moneyverse.account.model.dto.*;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.entities.AccountCategory;
import it.moneyverse.account.model.event.AccountDeletionEvent;
import it.moneyverse.account.model.repositories.AccountCategoryRepository;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.account.utils.mapper.AccountMapper;
import it.moneyverse.core.enums.SortAttribute;
import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.beans.AccountDeletionTopic;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.core.services.MessageProducer;
import it.moneyverse.core.services.UserServiceClient;
import it.moneyverse.core.services.UserServiceGrpcClient;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AccountManagementService implements AccountService {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountManagementService.class);
  private final AccountRepository accountRepository;
  private final AccountCategoryRepository accountCategoryRepository;
  private final UserServiceClient userServiceClient;
  private final CurrencyServiceClient currencyServiceClient;
  private final MessageProducer<UUID, String> messageProducer;

  public AccountManagementService(
      AccountRepository accountRepository,
      AccountCategoryRepository accountCategoryRepository,
      UserServiceGrpcClient userServiceClient,
      CurrencyServiceClient currencyServiceClient,
      MessageProducer<UUID, String> messageProducer) {
    this.accountRepository = accountRepository;
    this.accountCategoryRepository = accountCategoryRepository;
    this.userServiceClient = userServiceClient;
    this.currencyServiceClient = currencyServiceClient;
    this.messageProducer = messageProducer;
  }

  @Override
  @Transactional
  public AccountDto createAccount(AccountRequestDto request) {
    checkIfUserExists(request.userId());
    checkIfCurrencyExists(request.currency());
    checkIfAccountAlreadyExists(request.userId(), request.accountName());
    AccountCategory category = findAccountCategory(request.accountCategory());
    LOGGER.info("Creating account {} for user {}", request.accountName(), request.userId());
    Account account = AccountMapper.toAccount(request, category);
    if (accountRepository.findDefaultAccountsByUserId(request.userId()).isEmpty()) {
      LOGGER.info("Setting default account for user {}", request.userId());
      account.setDefault(Boolean.TRUE);
    } else {
      account.setDefault(Boolean.FALSE);
    }
    AccountDto result = AccountMapper.toAccountDto(accountRepository.save(account));
    LOGGER.info("Saved account {} for user {}", result.getAccountId(), request.userId());
    return result;
  }

  private void checkIfUserExists(UUID userId) {
    if (Boolean.FALSE.equals(userServiceClient.checkIfUserExists(userId))) {
      throw new ResourceNotFoundException("User %s does not exists".formatted(userId));
    }
  }

  private void checkIfCurrencyExists(String currency) {
    if (Boolean.FALSE.equals(currencyServiceClient.checkIfCurrencyExists(currency))) {
      throw new ResourceNotFoundException("Currency %s does not exists".formatted(currency));
    }
  }

  private void checkIfAccountAlreadyExists(UUID userId, String accountName) {
    if (Boolean.TRUE.equals(accountRepository.existsByUserIdAndAccountName(userId, accountName))) {
      throw new ResourceAlreadyExistsException(
          "Account with name %s already exists".formatted(accountName));
    }
  }

  @Override
  @Transactional(readOnly = true)
  public List<AccountDto> findAccounts(UUID userId, AccountCriteria criteria) {
    if (criteria.getPage() == null) {
      criteria.setPage(new PageCriteria());
    }
    if (criteria.getSort() == null) {
      criteria.setSort(
          new SortCriteria<>(
              SortAttribute.getDefault(AccountSortAttributeEnum.class), Direction.ASC));
    }
    LOGGER.info("Finding accounts with filters: {}", criteria);
    return AccountMapper.toAccountDto(accountRepository.findAccounts(userId, criteria));
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
    AccountCategory category =
        request.accountCategory() != null ? findAccountCategory(request.accountCategory()) : null;
    if (request.currency() != null) {
      checkIfCurrencyExists(request.currency());
    }
    account = AccountMapper.partialUpdate(account, request, category);
    if (Boolean.TRUE.equals(request.isDefault())) {
      accountRepository.findDefaultAccountsByUserId(account.getUserId()).stream()
          .filter(defaultAcc -> !defaultAcc.getAccountId().equals(accountId))
          .forEach(
              defaultAcc -> {
                defaultAcc.setDefault(false);
                accountRepository.save(defaultAcc);
              });
    }
    AccountDto result = AccountMapper.toAccountDto(accountRepository.save(account));
    LOGGER.info("Updated account {} for user {}", result.getAccountId(), account.getUserId());
    return result;
  }

  private AccountCategory findAccountCategory(String name) {
    return accountCategoryRepository
        .findByName(name)
        .orElseThrow(() -> new ResourceNotFoundException("Invalid account category"));
  }

  @Override
  @Transactional
  public void deleteAccount(UUID accountId) {
    Account account = findAccountById(accountId);
    accountRepository.delete(account);
    messageProducer.send(
        new AccountDeletionEvent(accountId, account.getUserId()), AccountDeletionTopic.TOPIC);
    LOGGER.info("Deleted account {} for user {}", account.getAccountId(), account.getUserId());
  }

  @Transactional
  @Override
  public void deleteAccountsByUserId(UUID userId) {
    LOGGER.info("Deleting accounts by user ID {}", userId);
    accountRepository.deleteAll(accountRepository.findAccountByUserId(userId));
  }

  @Transactional(readOnly = true)
  @Override
  public List<AccountCategoryDto> getAccountCategories() {
    return accountCategoryRepository.findAll().stream()
        .map(AccountMapper::toAccountCategoryDto)
        .toList();
  }

  private Account findAccountById(UUID accountId) {
    return accountRepository
        .findById(accountId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Account %s not found".formatted(accountId)));
  }
}
