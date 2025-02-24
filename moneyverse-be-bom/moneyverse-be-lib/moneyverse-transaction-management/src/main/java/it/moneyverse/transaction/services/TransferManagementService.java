package it.moneyverse.transaction.services;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.dto.AccountDto;
import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.transaction.exceptions.AccountTransferException;
import it.moneyverse.transaction.model.dto.TransferDto;
import it.moneyverse.transaction.model.dto.TransferRequestDto;
import it.moneyverse.transaction.model.dto.TransferUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.entities.Transfer;
import it.moneyverse.transaction.model.repositories.TransferRepository;
import it.moneyverse.transaction.runtime.messages.TransactionEventPublisher;
import it.moneyverse.transaction.utils.mapper.TransferMapper;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferManagementService implements TransferService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransferManagementService.class);

  private final TransferRepository transferRepository;
  private final CurrencyServiceClient currencyServiceClient;
  private final AccountServiceClient accountServiceClient;
  private final TransactionEventPublisher transactionEventPublisher;

  public TransferManagementService(
      TransferRepository transferRepository,
      CurrencyServiceClient currencyServiceClient,
      AccountServiceClient accountServiceClient,
      TransactionEventPublisher transactionEventPublisher) {
    this.transferRepository = transferRepository;
    this.currencyServiceClient = currencyServiceClient;
    this.accountServiceClient = accountServiceClient;
    this.transactionEventPublisher = transactionEventPublisher;
  }

  @Override
  @Transactional
  public TransferDto createTransfer(TransferRequestDto request) {
    checkSourceAndDestinationAccounts(request.fromAccount(), request.toAccount());
    currencyServiceClient.checkIfCurrencyExists(request.currency());
    AccountDto accountFrom = accountServiceClient.getAccountById(request.fromAccount());
    AccountDto accountTo = accountServiceClient.getAccountById(request.toAccount());
    LOGGER.info(
        "Creating transfer from account {} to account {}",
        request.fromAccount(),
        request.toAccount());
    BigDecimal normalizedAmount =
        currencyServiceClient.convertCurrencyAmountByUserPreference(
            request.userId(), request.amount(), request.currency(), request.date());
    Transfer transfer =
        transferRepository.save(
            createTransfer(
                request,
                createTransactionFrom(request, accountFrom, normalizedAmount),
                createTransactionTo(request, accountTo, normalizedAmount)));
    transactionEventPublisher.publishEvent(transfer, EventTypeEnum.CREATE);
    return TransferMapper.toTransferDto(transfer);
  }

  private Transaction createTransactionFrom(
      TransferRequestDto request, AccountDto accountFrom, BigDecimal normalizedAmount) {
    Transaction transaction = new Transaction();
    transaction.setUserId(request.userId());
    transaction.setAccountId(accountFrom.getAccountId());
    transaction.setDate(request.date());
    transaction.setDescription("Transfer to " + request.toAccount());
    transaction.setCurrency(accountFrom.getCurrency());
    if (!request.currency().equalsIgnoreCase(accountFrom.getCurrency())) {
      transaction.setAmount(
          currencyServiceClient
              .convertAmount(
                  request.amount(), request.currency(), accountFrom.getCurrency(), request.date())
              .multiply(BigDecimal.valueOf(-1)));
    } else {
      transaction.setAmount(request.amount().multiply(BigDecimal.valueOf(-1)));
    }
    transaction.setNormalizedAmount(normalizedAmount.multiply(BigDecimal.valueOf(-1)));
    return transaction;
  }

  private Transaction createTransactionTo(
      TransferRequestDto request, AccountDto accountTo, BigDecimal normalizedAmount) {
    Transaction transaction = new Transaction();
    transaction.setUserId(request.userId());
    transaction.setAccountId(accountTo.getAccountId());
    transaction.setDate(request.date());
    transaction.setDescription("Transfer from " + request.fromAccount());
    transaction.setCurrency(accountTo.getCurrency());
    if (!request.currency().equalsIgnoreCase(accountTo.getCurrency())) {
      transaction.setAmount(
          currencyServiceClient.convertAmount(
              request.amount(), request.currency(), accountTo.getCurrency(), request.date()));
    } else {
      transaction.setAmount(request.amount());
    }
    transaction.setNormalizedAmount(normalizedAmount);
    return transaction;
  }

  private Transfer createTransfer(
      TransferRequestDto request, Transaction transactionFrom, Transaction transactionTo) {
    Transfer transfer = new Transfer();
    transfer.setUserId(request.userId());
    transfer.setTransactionFrom(transactionFrom);
    transfer.setTransactionTo(transactionTo);
    transfer.setDate(request.date());
    transfer.setAmount(request.amount());
    transfer.setCurrency(request.currency());
    return transfer;
  }

  @Override
  @Transactional
  public TransferDto updateTransfer(UUID transferId, TransferUpdateRequestDto request) {
    if (request.currency() != null) {
      currencyServiceClient.checkIfCurrencyExists(request.currency());
    }
    if (request.fromAccount() != null && request.toAccount() != null) {
      checkSourceAndDestinationAccounts(request.fromAccount(), request.toAccount());
    }
    Transfer transfer = findTransfer(transferId);
    LOGGER.info("Updating transfer {}", transferId);
    transfer = TransferMapper.partialUpdate(transfer, request);
    updateTransactions(transfer, request);
    transfer = transferRepository.save(transfer);
    transactionEventPublisher.publishEvent(transfer, EventTypeEnum.UPDATE);
    return TransferMapper.toTransferDto(transfer);
  }

  private void updateTransactions(Transfer transfer, TransferUpdateRequestDto request) {
    if (request.currency() != null
        || request.fromAccount() != null
        || request.toAccount() != null
        || request.date() != null
        || request.amount() != null) {
      UUID fromAccountId =
          request.fromAccount() != null
              ? request.fromAccount()
              : transfer.getTransactionFrom().getAccountId();
      UUID toAccountId =
          request.toAccount() != null
              ? request.toAccount()
              : transfer.getTransactionTo().getAccountId();
      AccountDto accountFrom = accountServiceClient.getAccountById(fromAccountId);
      AccountDto accountTo = accountServiceClient.getAccountById(toAccountId);
      updateTransactions(transfer, accountFrom, accountTo);
    }
  }

  private void updateTransactions(Transfer transfer, AccountDto accountFrom, AccountDto accountTo) {
    BigDecimal amount = transfer.getAmount();
    BigDecimal normalizedAmount =
        currencyServiceClient.convertCurrencyAmountByUserPreference(
            transfer.getUserId(), amount, transfer.getCurrency(), transfer.getDate());
    BigDecimal amountFrom;
    if (!transfer.getCurrency().equalsIgnoreCase(accountFrom.getCurrency())) {
      amountFrom =
          currencyServiceClient
              .convertAmount(
                  amount, transfer.getCurrency(), accountFrom.getCurrency(), transfer.getDate())
              .multiply(BigDecimal.valueOf(-1));
    } else {
      amountFrom = amount.multiply(BigDecimal.valueOf(-1));
    }
    BigDecimal amountTo;
    if (!transfer.getCurrency().equalsIgnoreCase(accountTo.getCurrency())) {
      amountTo =
          currencyServiceClient.convertAmount(
              amount, transfer.getCurrency(), accountTo.getCurrency(), transfer.getDate());
    } else {
      amountTo = amount;
    }
    transfer.getTransactionFrom().setAmount(amountFrom);
    transfer
        .getTransactionFrom()
        .setNormalizedAmount(normalizedAmount.multiply(BigDecimal.valueOf(-1)));
    transfer.getTransactionTo().setAmount(amountTo);
    transfer.getTransactionTo().setNormalizedAmount(normalizedAmount);
  }

  @Override
  @Transactional
  public void deleteTransfer(UUID transferId) {
    Transfer transfer = findTransfer(transferId);
    LOGGER.info("Deleting transfer {}", transferId);
    transferRepository.delete(transfer);
    transactionEventPublisher.publishEvent(transfer, EventTypeEnum.DELETE);
  }

  @Override
  @Transactional(readOnly = true)
  public TransferDto getTransactionsByTransferId(UUID transferId) {
    Transfer transfer = findTransfer(transferId);
    LOGGER.info("Getting transactions by transfer {}", transferId);
    return TransferMapper.toTransferDto(transfer);
  }

  private void checkSourceAndDestinationAccounts(UUID sourceAccountId, UUID destinationAccountId) {
    if (sourceAccountId.equals(destinationAccountId)) {
      throw new AccountTransferException("The source and destination accounts must be different.");
    }
  }

  private Transfer findTransfer(UUID transferId) {
    return transferRepository
        .findById(transferId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Transfer %s not found".formatted(transferId)));
  }

  @Override
  @Transactional
  public void deleteAllTransfersByUserId(UUID userId) {
    LOGGER.info("Deleting transfers by user id {}", userId);
    List<Transfer> transfers = transferRepository.findTransferByUserId(userId);
    transferRepository.deleteAll(transfers);
  }

  @Override
  @Transactional
  public void deleteAllTransfersByAccountId(UUID accountId) {
    LOGGER.info("Deleting transfers by account id {}", accountId);
    List<Transfer> transfers = transferRepository.findTransferByAccountId(accountId);
    transferRepository.deleteAll(transfers);
    for (Transfer transfer : transfers) {
      transactionEventPublisher.publishEvent(transfer, EventTypeEnum.DELETE);
    }
  }
}
