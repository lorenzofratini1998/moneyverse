package it.moneyverse.core.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = PagedResponseDto.Builder.class)
public class PagedResponseDto<T> {
  private final PageMetadataDto metadata;
  private final List<T> content;

  public static class Builder<T> {
    private PageMetadataDto metadata;
    private List<T> content;

    public Builder<T> withMetadata(PageMetadataDto metadata) {
      this.metadata = metadata;
      return this;
    }

    public Builder<T> withContent(List<T> content) {
      this.content = content;
      return this;
    }

    public PagedResponseDto<T> build() {
      return new PagedResponseDto<>(this);
    }
  }

  public static <T> Builder<T> builder() {
    return new Builder<>();
  }

  public PagedResponseDto(Builder<T> builder) {
    this.metadata = builder.metadata;
    this.content = builder.content;
  }

  public PageMetadataDto getMetadata() {
    return metadata;
  }

  public List<T> getContent() {
    return content;
  }
}
