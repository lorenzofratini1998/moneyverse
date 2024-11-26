package it.moneyverse.model.entities;

import it.moneyverse.enums.UserRoleEnum;
import java.util.UUID;

public interface UserModel {

  UUID getUserId();
  String getName();
  String getSurname();
  String getEmail();
  String getUsername();
  String getPassword();
  UserRoleEnum getRole();

}
