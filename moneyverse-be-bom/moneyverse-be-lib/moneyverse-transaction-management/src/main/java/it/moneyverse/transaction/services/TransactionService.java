package it.moneyverse.transaction.services;

import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;

public interface TransactionService {

  TransactionDto createTransaction(TransactionRequestDto request);
}
