package it.moneyverse.test.extensions.testcontainers;

public class ClickhouseContainer extends org.testcontainers.clickhouse.ClickHouseContainer {

  private static final String CLICKHOUSE = "clickhouse";
  private static final String CLICKHOUSE_CLICKHOUSE_SERVER = "clickhouse/clickhouse-server";
  private static final String VERSION = "24.8";

  public ClickhouseContainer() {
    this(CLICKHOUSE_CLICKHOUSE_SERVER + ":" + VERSION);
  }

  public ClickhouseContainer(String dockerImageName) {
    super(dockerImageName);
    super.withDatabaseName(CLICKHOUSE);
    super.withUsername(CLICKHOUSE);
    super.withPassword(CLICKHOUSE);
  }
}
