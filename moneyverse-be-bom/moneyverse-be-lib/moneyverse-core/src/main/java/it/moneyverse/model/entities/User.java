package it.moneyverse.model.entities;

import it.moneyverse.enums.UserRoleEnum;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(name = "USERS")
public class User extends Auditable implements Serializable {

  @Serial
  private static final long serialVersionUID = 1L;

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "USER_ID", columnDefinition = "UUID")
  private UUID userId;

  @Column(name = "NAME", nullable = false)
  private String name;

  @Column(name = "SURNAME", nullable = false)
  private String surname;

  @Column(name = "USERNAME", unique = true)
  private String username;

  @Column(name = "EMAIL", unique = true)
  private String email;

  @Column(name = "PASSWORD")
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(name = "ROLE", nullable = false)
  private UserRoleEnum role;

  public UUID getUserId() {
    return userId;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getSurname() {
    return surname;
  }

  public void setSurname(String surname) {
    this.surname = surname;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public UserRoleEnum getRole() {
    return role;
  }

  public void setRole(UserRoleEnum role) {
    this.role = role;
  }
}
