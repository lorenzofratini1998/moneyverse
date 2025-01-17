package it.moneyverse.transaction.model.dto;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.io.Serializable;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = TagDto.Builder.class)
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "tagId")
public class TagDto implements Serializable {

  private final UUID tagId;
  private final String username;
  private final String tagName;
  private final String description;

  public TagDto(Builder builder) {
    this.tagId = builder.tagId;
    this.username = builder.username;
    this.tagName = builder.tagName;
    this.description = builder.description;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static class Builder {
    private UUID tagId;
    private String username;
    private String tagName;
    private String description;

    public Builder withTagId(UUID tagId) {
      this.tagId = tagId;
      return this;
    }

    public Builder withUsername(String username) {
      this.username = username;
      return this;
    }

    public Builder withTagName(String tagName) {
      this.tagName = tagName;
      return this;
    }

    public Builder withDescription(String description) {
      this.description = description;
      return this;
    }

    public TagDto build() {
      return new TagDto(this);
    }
  }

  public UUID getTagId() {
    return tagId;
  }

  public String getUsername() {
    return username;
  }

  public String getTagName() {
    return tagName;
  }

  public String getDescription() {
    return description;
  }
}
