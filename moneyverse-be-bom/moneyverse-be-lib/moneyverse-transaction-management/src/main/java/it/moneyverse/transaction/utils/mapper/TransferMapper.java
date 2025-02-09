package it.moneyverse.transaction.utils.mapper;

import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransferRequestDto;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.entities.Transfer;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

public class TransferMapper {

  public static Transfer toTransfer(TransferRequestDto request) {
    if (request == null) {
      return null;
    }
    Transfer transfer = new Transfer();
    transfer.setTransactionFrom(createTransactionFrom(request));
    transfer.setTransactionTo(createTransactionTo(request));
    transfer.setDate(request.date());
    transfer.setAmount(request.amount());
    transfer.setCurrency(request.currency());
    return transfer;
  }

  private static Transaction createTransactionFrom(TransferRequestDto request) {
    Transaction transactionFrom = new Transaction();
    transactionFrom.setUserId(request.userId());
    transactionFrom.setAccountId(request.fromAccount());
    transactionFrom.setDate(request.date());
    transactionFrom.setAmount(request.amount().multiply(BigDecimal.valueOf(-1)));
    transactionFrom.setDescription("Transfer to " + request.toAccount());
    transactionFrom.setCurrency(request.currency());
    return transactionFrom;
  }

  private static Transaction createTransactionTo(TransferRequestDto request) {
    Transaction transactionTo = new Transaction();
    transactionTo.setUserId(request.userId());
    transactionTo.setAccountId(request.toAccount());
    transactionTo.setDate(request.date());
    transactionTo.setAmount(request.amount());
    transactionTo.setDescription("Transfer from " + request.fromAccount());
    transactionTo.setCurrency(request.currency());
    return transactionTo;
  }

  public static List<TransactionDto> toTransactionDto(Transfer transfer) {
    if (transfer == null) {
      return Collections.emptyList();
    }
    return TransactionMapper.toTransactionDto(
        List.of(transfer.getTransactionFrom(), transfer.getTransactionTo()));
  }

  private TransferMapper() {}
}
