package it.moneyverse.budget.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.moneyverse.core.utils.JsonUtils;
import java.io.Serializable;
import java.util.UUID;
import org.openapitools.jackson.nullable.JsonNullable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_ABSENT)
public record CategoryUpdateRequestDto(
    String categoryName, String description, JsonNullable<UUID> parentId) implements Serializable {

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
