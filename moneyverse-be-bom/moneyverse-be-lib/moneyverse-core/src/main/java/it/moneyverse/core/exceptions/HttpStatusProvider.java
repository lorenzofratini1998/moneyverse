package it.moneyverse.core.exceptions;

import org.springframework.http.HttpStatus;

public interface HttpStatusProvider {
  HttpStatus getStatus();
}
