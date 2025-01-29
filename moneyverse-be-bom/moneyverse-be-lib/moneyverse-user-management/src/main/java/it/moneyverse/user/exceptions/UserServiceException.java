package it.moneyverse.user.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class UserServiceException extends RuntimeException {

  public UserServiceException(String message, Throwable cause) {
    super(message, cause);
  }
}
