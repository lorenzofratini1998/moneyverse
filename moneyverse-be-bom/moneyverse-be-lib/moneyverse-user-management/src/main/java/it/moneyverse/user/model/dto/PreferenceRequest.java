package it.moneyverse.user.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.moneyverse.core.utils.JsonUtils;
import it.moneyverse.user.enums.PreferenceKeyEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PreferenceRequest(
    @NotNull(message = "'key' cannot be null") PreferenceKeyEnum key,
    @NotEmpty(message = "'value' cannot be null or empty") String value) {
  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
