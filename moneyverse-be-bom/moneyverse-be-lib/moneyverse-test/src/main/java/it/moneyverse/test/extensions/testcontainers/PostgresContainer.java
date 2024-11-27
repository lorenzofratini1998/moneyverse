package it.moneyverse.test.extensions.testcontainers;

import org.testcontainers.containers.PostgreSQLContainer;

public class PostgresContainer extends PostgreSQLContainer<PostgresContainer> {

  private static final String POSTGRES = "postgres";
  private static final String VERSION = "16.3";

  public PostgresContainer() {
    super(POSTGRES + ":" + VERSION);
  }

  public PostgresContainer(String dockerImageName) {
    super(dockerImageName);
    super.withDatabaseName(POSTGRES);
    super.withUsername(POSTGRES);
    super.withPassword(POSTGRES);
    super.withCommand("postgres", "-c", "max_connections=200");
  }

}
