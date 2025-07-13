package it.moneyverse.account.services;

import it.moneyverse.account.enums.AccountSortAttributeEnum;
import it.moneyverse.account.model.dto.*;
import it.moneyverse.account.model.entities.Account;
import it.moneyverse.account.model.entities.AccountCategory;
import it.moneyverse.account.model.repositories.AccountCategoryRepository;
import it.moneyverse.account.model.repositories.AccountRepository;
import it.moneyverse.account.runtime.messages.AccountEventPublisher;
import it.moneyverse.account.utils.mapper.AccountMapper;
import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.enums.SortAttribute;
import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.exceptions.ResourceUpdateException;
import it.moneyverse.core.model.dto.AccountDto;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.model.dto.SortCriteria;
import it.moneyverse.core.services.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.function.BinaryOperator;
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
  private final CurrencyServiceClient currencyServiceClient;
  private final AccountEventPublisher eventPublisher;
  private final UserServiceClient userServiceClient;

  public AccountManagementService(
      AccountRepository accountRepository,
      AccountCategoryRepository accountCategoryRepository,
      CurrencyServiceClient currencyServiceClient,
      AccountEventPublisher eventPublisher,
      UserServiceClient userServiceClient) {
    this.accountRepository = accountRepository;
    this.accountCategoryRepository = accountCategoryRepository;
    this.currencyServiceClient = currencyServiceClient;
    this.eventPublisher = eventPublisher;

    this.userServiceClient = userServiceClient;
  }

  @Override
  @Transactional
  public AccountDto createAccount(AccountRequestDto request) {
    currencyServiceClient.checkIfCurrencyExists(request.currency());
    checkIfAccountAlreadyExists(request.userId(), request.accountName());
    AccountCategory category = findAccountCategory(request.accountCategory());
    LOGGER.info("Creating account {} for user {}", request.accountName(), request.userId());
    Account account = AccountMapper.toAccount(request, category);
    if (accountRepository.findDefaultAccountByUserId(request.userId()).isEmpty()) {
      LOGGER.info("Setting default account for user {}", request.userId());
      account.setDefault(Boolean.TRUE);
    } else {
      account.setDefault(Boolean.FALSE);
    }
    AccountDto result = AccountMapper.toAccountDto(accountRepository.save(account));
    LOGGER.info("Saved account {} for user {}", result.getAccountId(), request.userId());
    return result;
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
    LOGGER.info("Finding accounts with filters: {}", criteria);
    if (criteria.getPage() == null) {
      PageCriteria page = new PageCriteria();
      page.setOffset(0);
      page.setLimit(Integer.MAX_VALUE);
      criteria.setPage(page);
    }
    if (criteria.getSort() == null) {
      criteria.setSort(
          new SortCriteria<>(
              SortAttribute.getDefault(AccountSortAttributeEnum.class), Direction.ASC));
    }
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

    if (Boolean.TRUE.equals(account.isDefault()) && Boolean.FALSE.equals(request.isDefault())) {
      throw new ResourceUpdateException("The default account cannot be disabled");
    }

    account = accountRepository.save(AccountMapper.partialUpdate(account, request, category));

    if (Boolean.TRUE.equals(request.isDefault())) {
      accountRepository.unsetDefaultAccountExcept(accountId);
    }
    AccountDto result = AccountMapper.toAccountDto(account);
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
    eventPublisher.publish(account, EventTypeEnum.DELETE);
    LOGGER.info("Deleted account {} for user {}", account.getAccountId(), account.getUserId());
  }

  @Transactional
  @Override
  public void deleteAccountsByUserId(UUID userId) {
    userServiceClient.checkIfUserStillExist(userId);
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

  @Override
  @Transactional
  public void incrementAccountBalance(
      UUID accountId, BigDecimal amount, String currency, LocalDate date) {
    updateAccountBalance(accountId, amount, currency, date, BigDecimal::add);
    LOGGER.info("Account balance for account {} increased by {}", accountId, amount);
  }

  @Override
  @Transactional
  public void decrementAccountBalance(
      UUID accountId, BigDecimal amount, String currency, LocalDate date) {
    updateAccountBalance(accountId, amount, currency, date, BigDecimal::subtract);
    LOGGER.info("Account balance for account {} decreased by {}", accountId, amount);
  }

  private void updateAccountBalance(
      UUID accountId,
      BigDecimal amount,
      String currency,
      LocalDate date,
      BinaryOperator<BigDecimal> operation) {
    Account account = findAccountById(accountId);
    BigDecimal effectiveAmount =
        account.getCurrency().equals(currency)
            ? amount
            : currencyServiceClient.convertAmount(amount, currency, account.getCurrency(), date);
    account.setBalance(operation.apply(account.getBalance(), effectiveAmount));
    accountRepository.save(account);
  }

  private Account findAccountById(UUID accountId) {
    return accountRepository
        .findById(accountId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Account %s not found".formatted(accountId)));
  }
}
