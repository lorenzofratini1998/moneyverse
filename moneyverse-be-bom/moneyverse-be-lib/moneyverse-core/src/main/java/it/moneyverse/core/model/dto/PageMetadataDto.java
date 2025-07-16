package it.moneyverse.core.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonDeserialize(builder = PageMetadataDto.Builder.class)
public class PageMetadataDto {
  private final Long totalElements;
  private final Integer totalPages;
  private final Integer size;
  private final Integer number;

  public static class Builder {
    private Long totalElements;
    private Integer totalPages;
    private Integer size;
    private Integer number;

    public Builder withTotalElements(Long totalElements) {
      this.totalElements = totalElements;
      return this;
    }

    public Builder withTotalPages(Integer totalPages) {
      this.totalPages = totalPages;
      return this;
    }

    public Builder withSize(Integer size) {
      this.size = size;
      return this;
    }

    public Builder withNumber(Integer number) {
      this.number = number;
      return this;
    }

    public PageMetadataDto build() {
      return new PageMetadataDto(this);
    }
  }

  public PageMetadataDto(Builder builder) {
    this.totalElements = builder.totalElements;
    this.totalPages = builder.totalPages;
    this.size = builder.size;
    this.number = builder.number;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Long getTotalElements() {
    return totalElements;
  }

  public Integer getTotalPages() {
    return totalPages;
  }

  public Integer getSize() {
    return size;
  }

  public Integer getNumber() {
    return number;
  }
}
