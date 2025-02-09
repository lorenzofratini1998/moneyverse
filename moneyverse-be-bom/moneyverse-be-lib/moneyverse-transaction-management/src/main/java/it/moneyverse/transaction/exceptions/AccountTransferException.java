package it.moneyverse.transaction.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountTransferException extends RuntimeException {

  public AccountTransferException(String message) {
    super(message);
  }
}
