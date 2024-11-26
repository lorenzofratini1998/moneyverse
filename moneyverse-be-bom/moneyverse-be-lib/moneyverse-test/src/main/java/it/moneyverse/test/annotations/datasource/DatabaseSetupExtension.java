package it.moneyverse.test.annotations.datasource;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class DatabaseSetupExtension implements BeforeEachCallback {

  private final CleanDatabaseExtension cleanDatabaseExtension = new CleanDatabaseExtension();
  private final PopulateDatabaseExtension populateDatabaseExtension = new PopulateDatabaseExtension();

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    cleanDatabaseExtension.beforeEach(context);
  }
}
