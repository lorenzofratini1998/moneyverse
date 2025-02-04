package it.moneyverse.user.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.moneyverse.core.utils.JsonUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record UserPreferenceRequest(
    @NotNull(message = "'preferenceId' cannot be null") UUID preferenceId,
    @NotEmpty(message = "'value' cannot be null or empty") String value) {
  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
