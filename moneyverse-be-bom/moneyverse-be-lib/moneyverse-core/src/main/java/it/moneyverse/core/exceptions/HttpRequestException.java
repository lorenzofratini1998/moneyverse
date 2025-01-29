package it.moneyverse.core.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class HttpRequestException extends RuntimeException {

    public HttpRequestException(String message) {
    super(message);
  }
}
