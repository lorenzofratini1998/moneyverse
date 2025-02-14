package it.moneyverse.transaction.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransferDto {
  private List<TransactionDto> transactions;

  public TransferDto() {}

  public TransferDto(List<TransactionDto> transactions) {
    this.transactions = transactions;
  }

  public List<TransactionDto> getTransactions() {
    return transactions;
  }

  public void setTransactions(List<TransactionDto> transactions) {
    this.transactions = transactions;
  }
}
