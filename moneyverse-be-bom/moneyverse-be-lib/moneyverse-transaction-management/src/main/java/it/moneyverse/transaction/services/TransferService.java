package it.moneyverse.transaction.services;

import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransferRequestDto;
import java.util.List;

public interface TransferService {

  List<TransactionDto> createTransfer(TransferRequestDto request);
}
