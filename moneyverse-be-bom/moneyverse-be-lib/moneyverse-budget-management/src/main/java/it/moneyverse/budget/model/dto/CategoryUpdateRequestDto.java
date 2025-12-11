package it.moneyverse.budget.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.moneyverse.core.model.dto.StyleRequestDto;
import it.moneyverse.core.utils.JsonUtils;
import java.io.Serializable;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CategoryUpdateRequestDto(
    String categoryName, String description, UUID parentId, StyleRequestDto style)
    implements Serializable {

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
