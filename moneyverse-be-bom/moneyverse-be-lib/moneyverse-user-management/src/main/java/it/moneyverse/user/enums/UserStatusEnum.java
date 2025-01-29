package it.moneyverse.user.enums;

public enum UserStatusEnum {
  ONBOARDING,
  ACTIVE,
  DISABLED;

  public static UserStatusEnum fromString(String status) {
    if (status == null) {
      throw new IllegalArgumentException("Status cannot be null");
    }
    try {
      return UserStatusEnum.valueOf(status.toUpperCase());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid UserStatusEnum value: " + status);
    }
  }
}
