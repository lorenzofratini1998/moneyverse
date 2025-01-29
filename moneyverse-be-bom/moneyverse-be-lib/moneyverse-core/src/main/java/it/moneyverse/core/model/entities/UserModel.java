package it.moneyverse.core.model.entities;

import it.moneyverse.core.enums.UserRoleEnum;
import java.util.Map;
import java.util.UUID;

public interface UserModel {

  UUID getUserId();

  void setUserId(UUID id);

  String getName();

  String getSurname();

  String getEmail();

  String getUsername();

  String getPassword();

  UserRoleEnum getRole();

  Map<String, String> getAttributes();
}
