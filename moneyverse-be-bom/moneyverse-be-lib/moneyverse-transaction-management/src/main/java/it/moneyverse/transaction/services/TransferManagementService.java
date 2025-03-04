package it.moneyverse.transaction.services;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.services.CurrencyServiceClient;
import it.moneyverse.core.services.UserServiceClient;
import it.moneyverse.transaction.model.dto.TransferDto;
import it.moneyverse.transaction.model.dto.TransferRequestDto;
import it.moneyverse.transaction.model.dto.TransferUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Transfer;
import it.moneyverse.transaction.model.factories.TransactionFactory;
import it.moneyverse.transaction.model.factories.TransferFactory;
import it.moneyverse.transaction.model.repositories.TransferRepository;
import it.moneyverse.transaction.model.validator.TransactionValidator;
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
  private final UserServiceClient userServiceClient;
  private final TransactionEventPublisher transactionEventPublisher;
  private final TransactionValidator transactionValidator;

  public TransferManagementService(
      TransferRepository transferRepository,
      CurrencyServiceClient currencyServiceClient,
      AccountServiceClient accountServiceClient,
      TransactionEventPublisher transactionEventPublisher,
      TransactionValidator transactionValidator,
      UserServiceClient userServiceClient) {
    this.transferRepository = transferRepository;
    this.currencyServiceClient = currencyServiceClient;
    this.accountServiceClient = accountServiceClient;
    this.transactionEventPublisher = transactionEventPublisher;
    this.transactionValidator = transactionValidator;
    this.userServiceClient = userServiceClient;
  }

  @Override
  @Transactional
  public TransferDto createTransfer(TransferRequestDto request) {
    transactionValidator.validate(request);
    LOGGER.info(
        "Creating transfer from account {} to account {}",
        request.fromAccount(),
        request.toAccount());
    BigDecimal normalizedAmount =
        currencyServiceClient.convertCurrencyAmountByUserPreference(
            request.userId(), request.amount(), request.currency(), request.date());
    Transfer transfer =
        transferRepository.save(
            TransferFactory.createTransfer(
                request,
                TransactionFactory.createDebitTransaction(request, normalizedAmount),
                TransactionFactory.createCreditTransaction(request, normalizedAmount)));
    transactionEventPublisher.publishEvent(transfer, EventTypeEnum.CREATE);
    return TransferMapper.toTransferDto(transfer);
  }

  @Override
  @Transactional
  public TransferDto updateTransfer(UUID transferId, TransferUpdateRequestDto request) {
    transactionValidator.validate(request);
    Transfer transfer = findTransfer(transferId);
    Transfer originalTransfer = transfer.copy();
    LOGGER.info("Updating transfer {}", transferId);
    transfer = updateTransfer(transfer, request);
    transactionEventPublisher.publishEvent(transfer, originalTransfer, EventTypeEnum.UPDATE);
    return TransferMapper.toTransferDto(transfer);
  }

  private Transfer updateTransfer(Transfer transfer, TransferUpdateRequestDto request) {
    transfer = TransferMapper.partialUpdate(transfer, request);
    if (request.amount() != null || request.currency() != null || request.date() != null) {
      BigDecimal normalizedAmount =
          currencyServiceClient.convertCurrencyAmountByUserPreference(
              transfer.getUserId(),
              transfer.getAmount(),
              transfer.getCurrency(),
              transfer.getDate());
      transfer.getTransactionFrom().setNormalizedAmount(normalizedAmount.negate());
      transfer.getTransactionTo().setNormalizedAmount(normalizedAmount);
    }
    return transferRepository.save(transfer);
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
    userServiceClient.checkIfUserStillExist(userId);
    List<Transfer> transfers = transferRepository.findTransferByUserId(userId);
    transferRepository.deleteAll(transfers);
  }

  @Override
  @Transactional
  public void deleteAllTransfersByAccountId(UUID accountId) {
    LOGGER.info("Deleting transfers by account id {}", accountId);
    accountServiceClient.checkIfAccountStillExists(accountId);
    List<Transfer> transfers = transferRepository.findTransferByAccountId(accountId);
    transferRepository.deleteAll(transfers);
    for (Transfer transfer : transfers) {
      transactionEventPublisher.publishEvent(transfer, EventTypeEnum.DELETE);
    }
  }
}
