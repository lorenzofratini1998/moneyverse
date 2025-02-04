package it.moneyverse.user.exceptions;

import it.moneyverse.core.enums.ErrorCategoryEnum;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.dto.ErrorDto;
import it.moneyverse.core.model.dto.ValidationErrorDto;
import it.moneyverse.core.utils.ErrorUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
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
public class UserExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserExceptionHandler.class);
  private static final String ERROR_MSG_PATTERN = "{}: {}";

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDto handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    LOGGER.error(ERROR_MSG_PATTERN, HttpStatus.BAD_REQUEST, "Client validation failed");
    return ErrorUtils.toErrorDto(request, ex);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorDto handleConstraintViolationException(
      ConstraintViolationException ex, HttpServletRequest request) {
    LOGGER.error(ERROR_MSG_PATTERN, HttpStatus.BAD_REQUEST, "Client validation failed");
    Set<ConstraintViolation<?>> violations = ex.getConstraintViolations();
    List<ValidationErrorDto> validationErrorResponse =
        violations.stream()
            .map(
                error -> {
                  String field = null;
                  for (Path.Node node : error.getPropertyPath()) {
                    field = node.toString();
                  }
                  return new ValidationErrorDto(field, error.getInvalidValue(), error.getMessage());
                })
            .toList();
    return ErrorDto.builder()
        .withTimestamp(LocalDateTime.now())
        .withStatus(HttpStatus.BAD_REQUEST)
        .withMethod(request.getMethod())
        .withPath(request.getRequestURI())
        .withCategory(ErrorCategoryEnum.CLIENT)
        .withValidationErrors(validationErrorResponse)
        .build();
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorDto handleResourceNotFoundException(
      ResourceNotFoundException ex, HttpServletRequest request) {
    LOGGER.error(ERROR_MSG_PATTERN, HttpStatus.NOT_FOUND, ex.getMessage());
    return ErrorUtils.toErrorDto(request, HttpStatus.NOT_FOUND, ex.getMessage());
  }
}
