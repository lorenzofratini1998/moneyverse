package it.moneyverse.core.model.dto;

import it.moneyverse.core.utils.constants.CommonConstants;
import jakarta.validation.constraints.NotNull;

public class PageCriteria {

  @NotNull
  private Integer offset;
  @NotNull
  private Integer limit;

  public PageCriteria() {
    this.offset = CommonConstants.DEFAULT_OFFSET;
    this.limit = CommonConstants.DEFAULT_LIMIT;
  }

  public Integer getOffset() {
    return offset;
  }

  public void setOffset(Integer offset) {
    this.offset = offset;
  }

  public Integer getLimit() {
    return limit;
  }

  public void setLimit(Integer limit) {
    this.limit = limit;
  }
}
