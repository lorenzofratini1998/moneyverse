package it.moneyverse.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ResourceStillExistsException extends RuntimeException {

  public ResourceStillExistsException(String message) {
    super(message);
  }
}
