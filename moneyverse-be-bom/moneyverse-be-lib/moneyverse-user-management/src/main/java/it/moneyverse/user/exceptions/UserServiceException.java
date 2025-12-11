package it.moneyverse.user.exceptions;

import it.moneyverse.core.exceptions.HttpStatusProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UserServiceException extends RuntimeException implements HttpStatusProvider {

  public UserServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  @Override
  public HttpStatus getStatus() {
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }
}
