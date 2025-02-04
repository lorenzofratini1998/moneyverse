package it.moneyverse.user.model.dto;

import it.moneyverse.core.utils.JsonUtils;

public record UserUpdateRequestDto(String firstName, String lastName, String email) {
  @Override
  public String toString() {
    return JsonUtils.toJson(this);
  }
}
