package it.moneyverse.transaction.services;

import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.model.events.TransactionEvent;
import it.moneyverse.transaction.exceptions.AccountTransferException;
import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransferRequestDto;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.entities.Transfer;
import it.moneyverse.transaction.model.repositories.TransferRepository;
import it.moneyverse.transaction.utils.mapper.TransferMapper;
import java.util.List;
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
  private final TransactionServiceHelper transactionServiceHelper;

  public TransferManagementService(
      ApplicationEventPublisher eventPublisher,
      TransferRepository transferRepository,
      TransactionServiceHelper transactionServiceHelper) {
    this.eventPublisher = eventPublisher;
    this.transferRepository = transferRepository;
    this.transactionServiceHelper = transactionServiceHelper;
  }

  @Override
  @Transactional
  public List<TransactionDto> createTransfer(TransferRequestDto request) {
    if (request.fromAccount().equals(request.toAccount())) {
      throw new AccountTransferException("The source and destination accounts must be different.");
    }
    transactionServiceHelper.checkIfUserExists(request.userId());
    transactionServiceHelper.checkIfAccountExists(request.fromAccount());
    transactionServiceHelper.checkIfAccountExists(request.toAccount());
    transactionServiceHelper.checkIfCurrencyExists(request.currency());
    LOGGER.info(
        "Creating transfer from account {} to account {}",
        request.fromAccount(),
        request.toAccount());
    Transfer transfer = transferRepository.save(TransferMapper.toTransfer(request));
    publishEvent(transfer);
    return TransferMapper.toTransactionDto(transfer);
  }

  private void publishEvent(Transfer transfer) {
    publishEvent(transfer.getTransactionFrom());
    publishEvent(transfer.getTransactionTo());
  }

  private void publishEvent(Transaction transaction) {
    TransactionEvent event = new TransactionEvent();
    event.setTransactionId(transaction.getTransactionId());
    event.setAccountId(transaction.getAccountId());
    event.setDate(transaction.getDate());
    event.setAmount(transaction.getAmount());
    event.setEventType(EventTypeEnum.CREATE);
    eventPublisher.publishEvent(event);
  }
}
