package it.moneyverse.transaction.services;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.events.TransactionEvent;
import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.transaction.exceptions.AccountTransferException;
import it.moneyverse.transaction.model.dto.TransferDto;
import it.moneyverse.transaction.model.dto.TransferRequestDto;
import it.moneyverse.transaction.model.dto.TransferUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.entities.Transfer;
import it.moneyverse.transaction.model.repositories.TransferRepository;
import it.moneyverse.transaction.utils.mapper.TransferMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TransferManagementService implements TransferService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TransferManagementService.class);

  private final ApplicationEventPublisher eventPublisher;
  private final TransferRepository transferRepository;
  private final CurrencyServiceClient currencyServiceClient;
  private final AccountServiceClient accountServiceClient;

  public TransferManagementService(
      ApplicationEventPublisher eventPublisher,
      TransferRepository transferRepository,
      CurrencyServiceClient currencyServiceClient,
      AccountServiceClient accountServiceClient) {
    this.eventPublisher = eventPublisher;
    this.transferRepository = transferRepository;
    this.currencyServiceClient = currencyServiceClient;
    this.accountServiceClient = accountServiceClient;
  }

  @Override
  @Transactional
  public TransferDto createTransfer(TransferRequestDto request) {
    checkSourceAndDestinationAccounts(request.fromAccount(), request.toAccount());
    accountServiceClient.checkIfAccountExists(request.fromAccount());
    accountServiceClient.checkIfAccountExists(request.toAccount());
    currencyServiceClient.checkIfCurrencyExists(request.currency());
    LOGGER.info(
        "Creating transfer from account {} to account {}",
        request.fromAccount(),
        request.toAccount());
    Transfer transfer = transferRepository.save(TransferMapper.toTransfer(request));
    publishEvent(transfer, EventTypeEnum.CREATE);
    return new TransferDto(TransferMapper.toTransactionDto(transfer));
  }

  @Override
  @Transactional
  public TransferDto updateTransfer(UUID transferId, TransferUpdateRequestDto request) {
    if (request.fromAccount() != null && request.toAccount() != null) {
      checkSourceAndDestinationAccounts(request.fromAccount(), request.toAccount());
      accountServiceClient.checkIfAccountExists(request.fromAccount());
      accountServiceClient.checkIfAccountExists(request.toAccount());
    }
    if (request.currency() != null) {
      currencyServiceClient.checkIfCurrencyExists(request.currency());
    }
    Transfer transfer = findTransfer(transferId);
    LOGGER.info("Updating transfer {}", transferId);
    transfer = transferRepository.save(TransferMapper.partialUpdate(transfer, request));
    publishEvent(transfer, EventTypeEnum.UPDATE);
    return new TransferDto(TransferMapper.toTransactionDto(transfer));
  }

  @Override
  @Transactional
  public void deleteTransfer(UUID transferId) {
    Transfer transfer = findTransfer(transferId);
    LOGGER.info("Deleting transfer {}", transferId);
    transferRepository.delete(transfer);
    publishEvent(transfer, EventTypeEnum.DELETE);
  }

  @Override
  @Transactional(readOnly = true)
  public TransferDto getTransactionsByTransferId(UUID transferId) {
    Transfer transfer = findTransfer(transferId);
    LOGGER.info("Getting transactions by transfer {}", transferId);
    return new TransferDto(TransferMapper.toTransactionDto(transfer));
  }

  private void publishEvent(Transfer transfer, EventTypeEnum eventType) {
    publishEvent(transfer.getTransactionFrom(), eventType);
    publishEvent(transfer.getTransactionTo(), eventType);
  }

  private void publishEvent(Transaction transaction, EventTypeEnum eventType) {
    TransactionEvent event = new TransactionEvent();
    event.setTransactionId(transaction.getTransactionId());
    event.setAccountId(transaction.getAccountId());
    event.setDate(transaction.getDate());
    event.setAmount(transaction.getAmount());
    event.setEventType(eventType);
    eventPublisher.publishEvent(event);
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
}
