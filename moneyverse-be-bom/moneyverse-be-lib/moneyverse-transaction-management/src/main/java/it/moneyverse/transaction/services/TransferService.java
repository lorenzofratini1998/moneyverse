package it.moneyverse.transaction.services;

import it.moneyverse.transaction.model.dto.TransferDto;
import it.moneyverse.transaction.model.dto.TransferRequestDto;
import it.moneyverse.transaction.model.dto.TransferUpdateRequestDto;
import java.util.UUID;

public interface TransferService {

  TransferDto createTransfer(TransferRequestDto request);

  TransferDto updateTransfer(UUID transferId, TransferUpdateRequestDto request);

  void deleteTransfer(UUID transferId);

  TransferDto getTransactionsByTransferId(UUID transferId);
}
