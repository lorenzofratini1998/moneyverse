package it.moneyverse.account.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import it.moneyverse.core.utils.JsonUtils;
import java.io.Serializable;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
public record AccountUpdateRequestDto(
    String accountName,
    BigDecimal balance,
    BigDecimal balanceTarget,
    String accountCategory,
    String accountDescription,
    Boolean isDefault)
    implements Serializable {
    @Override
    public String toString() {
        return JsonUtils.toJson(this);
    }
}
