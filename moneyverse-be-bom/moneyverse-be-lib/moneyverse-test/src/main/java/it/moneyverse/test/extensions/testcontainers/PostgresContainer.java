package it.moneyverse.test.extensions.testcontainers;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresContainer extends PostgreSQLContainer<PostgresContainer> {

  private static final String POSTGRES = "postgres";
  private static final String IMAGE_NAME = "%s:17.2".formatted(POSTGRES);

  public PostgresContainer() {
    super(IMAGE_NAME);
    super.withDatabaseName(POSTGRES);
    super.withUsername(POSTGRES);
    super.withPassword(POSTGRES);
    super.withCommand("postgres", "-c", "max_connections=200");
  }

}
