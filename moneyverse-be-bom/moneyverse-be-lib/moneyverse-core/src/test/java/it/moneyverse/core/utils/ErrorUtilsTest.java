package it.moneyverse.core.utils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import it.moneyverse.core.enums.ErrorCategoryEnum;
import it.moneyverse.core.model.dto.ErrorDto;
import it.moneyverse.core.model.dto.ValidationErrorDto;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

@ExtendWith(MockitoExtension.class)
class ErrorUtilsTest {

  @Test
  void testToErrorDto_WithValidationException(
      @Mock HttpServletRequest request,
      @Mock MethodArgumentNotValidException ex,
      @Mock BindingResult bindingResult) {
    List<ObjectError> objectErrors =
        List.of(new FieldError("object", "field", "rejectedValue", false, null, null, "message"));
    when(ex.getBindingResult()).thenReturn(bindingResult);
    when(ex.getBindingResult().getAllErrors()).thenReturn(objectErrors);
    when(request.getMethod()).thenReturn("POST");
    when(request.getRequestURI()).thenReturn("/api/test");

    ErrorDto result = ErrorUtils.toErrorDto(request, ex);

    assertNotNull(result);
    assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
    assertEquals("POST", result.getMethod());
    assertEquals("/api/test", result.getPath());
    assertEquals(ErrorCategoryEnum.CLIENT, result.getCategory());
    assertEquals(1, result.getValidationErrors().size());
    ValidationErrorDto validationErrorDto = result.getValidationErrors().getFirst();
    assertEquals("field", validationErrorDto.field());
    assertEquals("rejectedValue", validationErrorDto.rejectedValue());
    assertEquals("message", validationErrorDto.message());
  }

  @Test
  void testToErrorDto_WithMessage(@Mock HttpServletRequest request) {
    when(request.getMethod()).thenReturn("GET");
    when(request.getRequestURI()).thenReturn("/api/test");

    ErrorDto result =
        ErrorUtils.toErrorDto(request, HttpStatus.INTERNAL_SERVER_ERROR, "Server error");

    assertNotNull(result);
    assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatus());
    assertEquals("GET", result.getMethod());
    assertEquals("/api/test", result.getPath());
    assertEquals(ErrorCategoryEnum.SERVER, result.getCategory());
    assertEquals("Server error", result.getMessage());
  }

  @Test
  void testToErrorDto_WithClientError(@Mock HttpServletRequest request) {
    when(request.getMethod()).thenReturn("PUT");
    when(request.getRequestURI()).thenReturn("/api/test");

    ErrorDto result = ErrorUtils.toErrorDto(request, HttpStatus.BAD_REQUEST, "Bad Request");

    assertNotNull(result);
    assertEquals(HttpStatus.BAD_REQUEST, result.getStatus());
    assertEquals("PUT", result.getMethod());
    assertEquals("/api/test", result.getPath());
    assertEquals(ErrorCategoryEnum.CLIENT, result.getCategory());
    assertEquals("Bad Request", result.getMessage());
  }
}
