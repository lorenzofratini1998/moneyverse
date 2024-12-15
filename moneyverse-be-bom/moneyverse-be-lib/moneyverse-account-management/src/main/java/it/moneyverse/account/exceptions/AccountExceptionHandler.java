package it.moneyverse.account.exceptions;

import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.dto.ErrorDto;
import it.moneyverse.core.utils.ErrorUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseBody
public class AccountExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(AccountExceptionHandler.class);
  private static final String ERROR_MSG_PATTERN = "{}: {}";

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDto handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    LOGGER.error(ERROR_MSG_PATTERN, HttpStatus.BAD_REQUEST, "Client validation failed");
    return ErrorUtils.toErrorDto(request, ex);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorDto handleResourceNotFoundException(
      ResourceNotFoundException ex, HttpServletRequest request) {
    LOGGER.error(ERROR_MSG_PATTERN, HttpStatus.NOT_FOUND, ex.getMessage());
    return ErrorUtils.toErrorDto(request, HttpStatus.NOT_FOUND, ex.getMessage());
  }

  @ExceptionHandler(ResourceAlreadyExistsException.class)
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorDto handleResourceAlreadyExistsException(
      ResourceAlreadyExistsException ex, HttpServletRequest request) {
    LOGGER.error(ERROR_MSG_PATTERN, HttpStatus.CONFLICT, ex.getMessage());
    return ErrorUtils.toErrorDto(request, HttpStatus.CONFLICT, ex.getMessage());
  }
}
