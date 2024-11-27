package it.moneyverse.core.model.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.moneyverse.core.enums.ErrorCategoryEnum;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.http.HttpStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorDto {

  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd 'T' HH:mm:ss")
  private final LocalDateTime timestamp;
  private final HttpStatus status;
  private final Integer code;
  private final String method;
  private final String path;
  private final String message;
  private final ErrorCategoryEnum category;
  private final List<ValidationErrorDto> validationErrors;

  public ErrorDto(Builder builder) {
    this.timestamp = builder.timestamp;
    this.status = builder.status;
    this.code = builder.code;
    this.method = builder.method;
    this.path = builder.path;
    this.message = builder.message;
    this.category = builder.category;
    this.validationErrors = builder.validationErrors;
  }

  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public Integer getCode() {
    return code;
  }

  public String getMethod() {
    return method;
  }

  public String getPath() {
    return path;
  }

  public String getMessage() {
    return message;
  }

  public ErrorCategoryEnum getCategory() {
    return category;
  }

  public List<ValidationErrorDto> getValidationErrors() {
    return validationErrors;
  }

  public static class Builder {

    private LocalDateTime timestamp;
    private HttpStatus status;
    private Integer code;
    private String method;
    private String path;
    private String message;
    private ErrorCategoryEnum category;
    private List<ValidationErrorDto> validationErrors;

    public Builder timestamp(LocalDateTime timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder status(HttpStatus status) {
      this.status = status;
      return this;
    }

    public Builder code(Integer code) {
      this.code = code;
      return this;
    }

    public Builder method(String method) {
      this.method = method;
      return this;
    }

    public Builder path(String path) {
      this.path = path;
      return this;
    }

    public Builder message(String message) {
      this.message = message;
      return this;
    }

    public Builder category(ErrorCategoryEnum category) {
      this.category = category;
      return this;
    }

    public Builder validationErrors(List<ValidationErrorDto> validationErrors) {
      this.validationErrors = validationErrors;
      return this;
    }

    public ErrorDto build() {
      return new ErrorDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }
}
