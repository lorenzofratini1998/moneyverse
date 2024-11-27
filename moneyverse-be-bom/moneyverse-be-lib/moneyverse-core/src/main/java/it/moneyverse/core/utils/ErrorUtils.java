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

  public static ErrorDto toErrorDto(HttpServletRequest request,
      MethodArgumentNotValidException ex) {
    List<ObjectError> validationErrors = ex.getBindingResult().getAllErrors();
    List<ValidationErrorDto> validationErrorResponse = buildValidationErrors(validationErrors);
    return ErrorDto.builder().timestamp(LocalDateTime.now()).status(HttpStatus.BAD_REQUEST)
        .method(request.getMethod()).path(request.getRequestURI())
        .category(ErrorCategoryEnum.CLIENT).validationErrors(validationErrorResponse).build();
  }

  private static List<ValidationErrorDto> buildValidationErrors(
      List<ObjectError> validationErrors) {
    return validationErrors.stream().map(FieldError.class::cast).map(
        error -> new ValidationErrorDto(error.getField(), error.getRejectedValue(),
            error.getDefaultMessage())).toList();
  }

  private ErrorUtils() {
  }

}
