package it.moneyverse.core.model.dto;

import it.moneyverse.core.enums.SortAttribute;
import org.springframework.data.domain.Sort;

public class SortCriteria<T extends SortAttribute> {

  private T attribute;
  private Sort.Direction direction;

  public SortCriteria(T attribute, Sort.Direction direction) {
    this.attribute = attribute;
    this.direction = direction;
  }

  public SortAttribute getAttribute() {
    return attribute;
  }

  public void setAttribute(T attribute) {
    this.attribute = attribute;
  }

  public Sort.Direction getDirection() {
    return direction;
  }

  public void setDirection(Sort.Direction direction) {
    this.direction = direction;
  }
}
