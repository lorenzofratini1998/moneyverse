package it.moneyverse.core.exceptions;

import it.moneyverse.core.enums.ErrorCategoryEnum;
import it.moneyverse.core.model.dto.ErrorDto;
import it.moneyverse.core.model.dto.ValidationErrorDto;
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
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
@ResponseBody
public class MoneyverseExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(MoneyverseExceptionHandler.class);
  private static final String ERROR_MSG_PATTERN = "{}: {}";

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorDto> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    LOGGER.error(ERROR_MSG_PATTERN, HttpStatus.BAD_REQUEST, "Client validation failed");
    List<ValidationErrorDto> errors =
        ex.getBindingResult().getAllErrors().stream()
            .filter(FieldError.class::isInstance)
            .map(FieldError.class::cast)
            .map(
                error ->
                    new ValidationErrorDto(
                        error.getField(), error.getRejectedValue(), error.getDefaultMessage()))
            .toList();

    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            ErrorDto.builder()
                .withTimestamp(LocalDateTime.now())
                .withStatus(HttpStatus.BAD_REQUEST)
                .withMethod(request.getMethod())
                .withPath(request.getRequestURI())
                .withCategory(ErrorCategoryEnum.CLIENT)
                .withValidationErrors(errors)
                .build());
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

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorDto> handleResourceNotFound(
      RuntimeException ex, HttpServletRequest request) {
    HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
    if (ex instanceof HttpStatusProvider) {
      status = ((HttpStatusProvider) ex).getStatus();
    }
    LOGGER.error(ERROR_MSG_PATTERN, status, ex.getMessage());
    return ResponseEntity.status(status)
        .body(
            ErrorDto.builder()
                .withTimestamp(LocalDateTime.now())
                .withStatus(status)
                .withCode(status.value())
                .withMethod(request.getMethod())
                .withPath(request.getRequestURI())
                .withCategory(
                    status.is4xxClientError() ? ErrorCategoryEnum.CLIENT : ErrorCategoryEnum.SERVER)
                .withMessage(ex.getMessage())
                .build());
  }
}
