package it.moneyverse.transaction.runtime.controllers;

import it.moneyverse.transaction.model.dto.TransferDto;
import it.moneyverse.transaction.model.dto.TransferRequestDto;
import it.moneyverse.transaction.model.dto.TransferUpdateRequestDto;
import jakarta.validation.Valid;
import java.util.UUID;

public interface TransferOperations {
  TransferDto createTransfer(@Valid TransferRequestDto request);

  TransferDto updateTransfer(UUID transferId, TransferUpdateRequestDto request);

  void deleteTransfer(UUID transferId);

  TransferDto getTransactionsByTransferId(UUID transferId);
}
