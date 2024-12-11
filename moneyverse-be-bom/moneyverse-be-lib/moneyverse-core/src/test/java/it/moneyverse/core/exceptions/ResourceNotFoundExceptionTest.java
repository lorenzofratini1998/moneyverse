package it.moneyverse.core.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ResourceNotFoundExceptionTest {

  @Test
  void testExceptionMessage() {
    String message = "Resource not found";
    ResourceNotFoundException exception = new ResourceNotFoundException(message);

    assertEquals(message, exception.getMessage());
  }

  @Test
  void testExceptionStatus() {
    ResourceNotFoundException exception = new ResourceNotFoundException("Resource not found");
    ResponseStatus responseStatus = exception.getClass().getAnnotation(ResponseStatus.class);
    assertNotNull(responseStatus);
    assertEquals(HttpStatus.NOT_FOUND, responseStatus.value());
  }
}
