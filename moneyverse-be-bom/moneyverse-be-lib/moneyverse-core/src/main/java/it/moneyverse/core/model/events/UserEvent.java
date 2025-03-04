package it.moneyverse.core.model.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = UserEvent.Builder.class)
public class UserEvent extends AbstractEvent {

  private final UUID userId;
  private final String firstName;
  private final String lastName;
  private final String email;

  public UserEvent(Builder builder) {
    super(builder);
    this.userId = builder.userId;
    this.firstName = builder.firstName;
    this.lastName = builder.lastName;
    this.email = builder.email;
  }

  public static class Builder extends AbstractBuilder<UserEvent, Builder> {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;

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

    @Override
    protected Builder self() {
      return this;
    }

    @Override
    public UserEvent build() {
      return new UserEvent(this);
    }
  }

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public UUID key() {
    return userId;
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
}
