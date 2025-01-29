package it.moneyverse.user.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = UserDto.Builder.class)
public class UserDto implements Serializable {

  private final UUID userId;
  private final String firstName;
  private final String lastName;
  private final String email;
  private final String imageUrl;

  public UserDto(Builder builder) {
    this.userId = builder.userId;
    this.firstName = builder.firstName;
    this.lastName = builder.lastName;
    this.email = builder.email;
    this.imageUrl = builder.imageUrl;
  }

  public static class Builder {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String imageUrl;

    public Builder withUserId(UUID userId) {
      this.userId = userId;
      return this;
    }

    public Builder withFirstName(String firstName) {
      this.firstName = firstName;
      return this;
    }

    public Builder withLastName(String lastName) {
      this.lastName = lastName;
      return this;
    }

    public Builder withEmail(String email) {
      this.email = email;
      return this;
    }

    public Builder withImageUrl(String imageUrl) {
      this.imageUrl = imageUrl;
      return this;
    }

    public UserDto build() {
      return new UserDto(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  public UUID getUserId() {
    return userId;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public String getEmail() {
    return email;
  }

  public String getImageUrl() {
    return imageUrl;
  }
}
