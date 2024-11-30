package it.moneyverse.core.model.auth;

import java.security.Principal;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

public class AuthenticatedUser implements Principal {

  private final String id;
  private final String fullName;
  private final String username;
  private final String email;
  private final Boolean isEmailVerified;
  private final Collection<GrantedAuthority> authorities;

  public AuthenticatedUser(Builder builder) {
    this.id = builder.id;
    this.fullName = builder.name + " " + builder.surname;
    this.username = builder.username;
    this.email = builder.email;
    this.isEmailVerified = builder.isEmailVerified;
    this.authorities = builder.authorities;
  }

  public static class Builder {

    private String id;
    private String name;
    private String surname;
    private String username;
    private String email;
    private Boolean isEmailVerified;
    private Collection<GrantedAuthority> authorities;

    public Builder withId(String id) {
      this.id = id;
      return this;
    }

    public Builder withName(String name) {
      this.name = name;
      return this;
    }

    public Builder withSurname(String surname) {
      this.surname = surname;
      return this;
    }

    public Builder withUsername(String username) {
      this.username = username;
      return this;
    }

    public Builder withEmail(String email) {
      this.email = email;
      return this;
    }

    public Builder withIsEmailVerified(Boolean isEmailVerified) {
      this.isEmailVerified = isEmailVerified;
      return this;
    }

    public Builder withAuthorities(Collection<GrantedAuthority> authorities) {
      this.authorities = authorities;
      return this;
    }

    public AuthenticatedUser build() {
      return new AuthenticatedUser(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public String getId() {
    return id;
  }

  public String getFullName() {
    return fullName;
  }

  public String getUsername() {
    return username;
  }

  public String getEmail() {
    return email;
  }

  public Boolean getEmailVerified() {
    return isEmailVerified;
  }

  public Collection<GrantedAuthority> getAuthorities() {
    return authorities;
  }

  @Override
  public String getName() {
    return username;
  }
}
