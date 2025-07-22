package it.moneyverse.core.enums;

public enum EventTypeEnum {
  CREATE,
  UPDATE,
  DELETE;

  public Integer toInteger() {
    return ordinal();
  }

  public static EventTypeEnum fromInteger(Integer value) {
    return values()[value];
  }
}
