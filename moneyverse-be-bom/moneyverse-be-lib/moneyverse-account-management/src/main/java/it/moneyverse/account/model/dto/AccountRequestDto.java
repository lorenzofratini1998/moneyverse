package it.moneyverse.account.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.moneyverse.enums.AccountCategoryEnum;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AccountRequestDto(
    @NotNull(message = "'User id' must not be null") UUID userId,
    @NotEmpty(message = "'Account name' must not be empty or null") String accountName,
    BigDecimal balance,
    BigDecimal balanceTarget,
    @NotNull(message = "'Account Category' must not be null") AccountCategoryEnum accountCategory,
    String accountDescription,
    Boolean isDefault
) implements Serializable {

}
