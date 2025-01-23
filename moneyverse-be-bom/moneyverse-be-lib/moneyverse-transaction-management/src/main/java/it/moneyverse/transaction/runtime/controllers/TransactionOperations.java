package it.moneyverse.transaction.runtime.controllers;

import it.moneyverse.transaction.model.dto.TransactionCriteria;
import it.moneyverse.transaction.model.dto.TransactionDto;
import it.moneyverse.transaction.model.dto.TransactionRequestDto;
import jakarta.validation.Valid;
import java.util.List;

public interface TransactionOperations {

  TransactionDto createTransaction(@Valid TransactionRequestDto request);

  List<TransactionDto> getTransactions(TransactionCriteria criteria);
}
