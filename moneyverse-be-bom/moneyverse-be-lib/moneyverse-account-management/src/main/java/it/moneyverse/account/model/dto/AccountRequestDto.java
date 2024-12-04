package it.moneyverse.account.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.moneyverse.core.enums.AccountCategoryEnum;
import it.moneyverse.core.utils.JsonUtils;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AccountRequestDto(
    @NotNull(message = "'Username' must not be null")
        @Size(max = 64, message = "'Username' must not exceed 64 characters")
        String username,
    @NotEmpty(message = "'Account name' must not be empty or null") String accountName,
    BigDecimal balance,
    BigDecimal balanceTarget,
    @NotNull(message = "'Account Category' must not be null") AccountCategoryEnum accountCategory,
    String accountDescription,
    Boolean isDefault)
    implements Serializable {

  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
