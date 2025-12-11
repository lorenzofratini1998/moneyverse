package it.moneyverse.transaction.utils.mapper;

import it.moneyverse.transaction.model.dto.TransferDto;
import it.moneyverse.transaction.model.dto.TransferUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Transfer;
import java.math.BigDecimal;

public class TransferMapper {

  public static TransferDto toTransferDto(Transfer transfer) {
    if (transfer == null) {
      return null;
    }
    return TransferDto.builder()
        .withTransferId(transfer.getTransferId())
        .withUserId(transfer.getUserId())
        .withDate(transfer.getDate())
        .withAmount(transfer.getAmount())
        .withCurrency(transfer.getCurrency())
        .withTransactionFrom(TransactionMapper.toTransactionDto(transfer.getTransactionFrom()))
        .withTransactionTo(TransactionMapper.toTransactionDto(transfer.getTransactionTo()))
        .build();
  }

  public static Transfer partialUpdate(Transfer transfer, TransferUpdateRequestDto request) {
    if (request == null) {
      return transfer;
    }
    if (request.date() != null) {
      transfer.setDate(request.date());
      transfer.getTransactionFrom().setDate(request.date());
      transfer.getTransactionTo().setDate(request.date());
    }
    if (request.amount() != null) {
      transfer.setAmount(request.amount());
      transfer.getTransactionFrom().setAmount(request.amount().multiply(BigDecimal.valueOf(-1)));
      transfer.getTransactionTo().setAmount(request.amount());
    }
    if (request.currency() != null) {
      transfer.setCurrency(request.currency());
      transfer.getTransactionFrom().setCurrency(request.currency());
      transfer.getTransactionTo().setCurrency(request.currency());
    }
    if (request.fromAccount() != null) {
      transfer.getTransactionFrom().setAccountId(request.fromAccount());
    }
    if (request.toAccount() != null) {
      transfer.getTransactionTo().setAccountId(request.toAccount());
    }
    return transfer;
  }

  private TransferMapper() {}
}
