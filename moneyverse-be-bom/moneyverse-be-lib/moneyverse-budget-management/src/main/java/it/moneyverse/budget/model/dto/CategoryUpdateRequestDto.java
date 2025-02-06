package it.moneyverse.budget.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.moneyverse.core.utils.JsonUtils;
import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record CategoryUpdateRequestDto(String categoryName, String description)
    implements Serializable {
  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
