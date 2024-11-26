package it.moneyverse.test.annotations.testcontainers;

class Property {

  private final String key;
  private final String value;

  Property(Builder builder) {
    this.key = builder.key;
    this.value = builder.value;
  }

  static class Builder {

    private String key;
    private String value;

    public Builder withKey(String key) {
      this.key = key;
      return this;
    }

    public Builder withValue(String value) {
      this.value = value;
      return this;
    }

    public Property build() {
      return new Property(this);
    }
  }

  static Builder builder() {
    return new Builder();
  }

  @Override
  public String toString() {
    return "%s=%s".formatted(key, value);
  }
}
