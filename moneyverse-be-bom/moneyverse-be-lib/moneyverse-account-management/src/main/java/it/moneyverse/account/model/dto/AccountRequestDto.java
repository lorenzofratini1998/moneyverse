package it.moneyverse.account.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.moneyverse.core.model.dto.StyleRequestDto;
import it.moneyverse.core.utils.JsonUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AccountRequestDto(
    @NotNull(message = "'User ID' must not be null") UUID userId,
    @NotEmpty(message = "'Account name' must not be empty or null") String accountName,
    BigDecimal balance,
    BigDecimal balanceTarget,
    @NotEmpty(message = "'Account Category' must not be empty or null") String accountCategory,
    String accountDescription,
    @NotEmpty(message = "'Currency' must not be null") String currency,
    StyleRequestDto style)
    implements Serializable {

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
