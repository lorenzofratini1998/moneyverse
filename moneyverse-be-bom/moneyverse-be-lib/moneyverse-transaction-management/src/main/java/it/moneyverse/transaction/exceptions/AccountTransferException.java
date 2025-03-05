package it.moneyverse.transaction.exceptions;

import it.moneyverse.core.exceptions.HttpStatusProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class AccountTransferException extends RuntimeException implements HttpStatusProvider {

  public AccountTransferException(String message) {
    super(message);
  }

  @Override
  public HttpStatus getStatus() {
    return HttpStatus.BAD_REQUEST;
  }
}
