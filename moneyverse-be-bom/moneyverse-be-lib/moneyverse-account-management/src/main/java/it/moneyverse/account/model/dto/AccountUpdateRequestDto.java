package it.moneyverse.account.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import it.moneyverse.core.enums.AccountCategoryEnum;
import it.moneyverse.core.utils.JsonUtils;

import java.io.Serializable;
import java.math.BigDecimal;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public record AccountUpdateRequestDto(String accountName,
                                      BigDecimal balance,
                                      BigDecimal balanceTarget,
                                      AccountCategoryEnum accountCategory,
                                      String accountDescription,
                                      Boolean isDefault)
        implements Serializable {
    @Override
    public String toString() {
        return JsonUtils.toJson(this);
    }
}
