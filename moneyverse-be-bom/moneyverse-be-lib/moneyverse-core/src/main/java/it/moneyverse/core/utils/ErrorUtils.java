package it.moneyverse.core.utils;

import it.moneyverse.core.enums.ErrorCategoryEnum;
import it.moneyverse.core.model.dto.ErrorDto;
import it.moneyverse.core.model.dto.ValidationErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

public class ErrorUtils {

  private ErrorUtils() {}

  public static ErrorDto toErrorDto(
      HttpServletRequest request, MethodArgumentNotValidException ex) {
    List<ObjectError> validationErrors = ex.getBindingResult().getAllErrors();
    List<ValidationErrorDto> validationErrorResponse = buildValidationErrors(validationErrors);
    return ErrorDto.builder()
        .withTimestamp(LocalDateTime.now())
        .withStatus(HttpStatus.BAD_REQUEST)
        .withMethod(request.getMethod())
        .withPath(request.getRequestURI())
        .withCategory(ErrorCategoryEnum.CLIENT)
        .withValidationErrors(validationErrorResponse)
        .build();
  }

  public static ErrorDto toErrorDto(
      HttpServletRequest request, HttpStatus httpStatus, String message) {
    return ErrorDto.builder()
        .withTimestamp(LocalDateTime.now())
        .withStatus(httpStatus)
        .withMethod(request.getMethod())
        .withPath(request.getRequestURI())
        .withCategory(
            httpStatus.is4xxClientError() ? ErrorCategoryEnum.CLIENT : ErrorCategoryEnum.SERVER)
        .withMessage(message)
        .build();
  }

  private static List<ValidationErrorDto> buildValidationErrors(
      List<ObjectError> validationErrors) {
    return validationErrors.stream()
        .map(FieldError.class::cast)
        .map(
            error ->
                new ValidationErrorDto(
                    error.getField(), error.getRejectedValue(), error.getDefaultMessage()))
        .toList();
  }
}
