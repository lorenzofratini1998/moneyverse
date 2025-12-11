package it.moneyverse.transaction.model.factories;

import it.moneyverse.transaction.model.dto.TransferRequestDto;
import it.moneyverse.transaction.model.entities.Transaction;
import it.moneyverse.transaction.model.entities.Transfer;

public class TransferFactory {

  public static Transfer createTransfer(
      TransferRequestDto request, Transaction debitTransaction, Transaction creditTransaction) {
    Transfer transfer = new Transfer();
    transfer.setUserId(request.userId());
    transfer.setTransactionFrom(debitTransaction);
    transfer.setTransactionTo(creditTransaction);
    transfer.setDate(request.date());
    transfer.setAmount(request.amount());
    transfer.setCurrency(request.currency());

    debitTransaction.setTransfer(transfer);
    creditTransaction.setTransfer(transfer);
    return transfer;
  }

  private TransferFactory() {}
}
