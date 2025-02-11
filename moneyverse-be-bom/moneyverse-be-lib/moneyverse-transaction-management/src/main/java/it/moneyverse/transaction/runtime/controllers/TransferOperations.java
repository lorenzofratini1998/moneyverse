package it.moneyverse.transaction.runtime.controllers;

import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransferRequestDto;
import it.moneyverse.transaction.model.dto.TransferUpdateRequestDto;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

public interface TransferOperations {
  List<TransactionDto> createTransfer(@Valid TransferRequestDto request);

  List<TransactionDto> updateTransfer(UUID transferId, @Valid TransferUpdateRequestDto request);

  void deleteTransfer(UUID transferId);

  List<TransactionDto> getTransactionsByTransferId(UUID transferId);
}
