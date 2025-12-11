package it.moneyverse.core.model.dto;

import jakarta.validation.constraints.NotNull;

public class PageCriteria {

  @NotNull private Integer offset;
  @NotNull private Integer limit;

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
