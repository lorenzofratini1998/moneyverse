package it.moneyverse.core.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class ResourceAlreadyExistsExceptionTest {

  @Test
  void testExceptionMessage() {
    String message = "Resource already exists";
    ResourceAlreadyExistsException exception = new ResourceAlreadyExistsException(message);

    assertEquals(message, exception.getMessage());
  }

  @Test
  void testExceptionStatus() {
    ResourceAlreadyExistsException exception =
        new ResourceAlreadyExistsException("Resource already exists");
    ResponseStatus responseStatus = exception.getClass().getAnnotation(ResponseStatus.class);
    assertNotNull(responseStatus);
    assertEquals(HttpStatus.CONFLICT, responseStatus.value());
  }
}
