package it.moneyverse.budget.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.moneyverse.core.utils.JsonUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CategoryRequestDto(
    @NotNull(message = "'User ID' must not be null") UUID userId,
    UUID parentId,
    @NotEmpty(message = "'Category name' must not be empty or null") String categoryName,
    String description) {
  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
