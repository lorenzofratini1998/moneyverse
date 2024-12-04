package it.moneyverse.test.operations.mapping;

import it.moneyverse.test.model.TestContextModel;
import it.moneyverse.test.model.dto.ScriptMetadata;
import it.moneyverse.test.services.ScriptService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityScriptGenerator {

  public static final String SQL_SCRIPT_FILE_NAME = "script.sql";
  private static final Logger LOGGER = LoggerFactory.getLogger(EntityScriptGenerator.class);

  private final List<EntityProcessingStrategy> strategies = new ArrayList<>();
  private final TestContextModel model;
  private final ScriptMetadata metadata;
  private final ScriptService scriptService;

  private String script;

  public EntityScriptGenerator(
      TestContextModel model, ScriptMetadata metadata, ScriptService scriptService) {
    this.model = model;
    this.metadata = metadata;
    this.scriptService = scriptService;
  }

  public EntityScriptGenerator addStrategy(EntityProcessingStrategy strategy) {
    strategies.add(strategy);
    return this;
  }

  public void execute() {
    script = generateScript();
    saveFile();
  }

  private String generateScript() {
    StringBuilder script = new StringBuilder();
    for (Class<?> entity : metadata.getEntities()) {
      for (EntityProcessingStrategy strategy : strategies) {
        if (strategy.supports(entity)) {
          script.append(strategy.process(model, scriptService, entity));
        }
      }
    }
    return script.toString();
  }

  private void saveFile() {
    Path sqlFile = metadata.getDirectory().resolve(SQL_SCRIPT_FILE_NAME);
    try {
      Files.write(sqlFile, script.getBytes());
      LOGGER.info("Script saved correctly to {}", sqlFile);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write script file", e);
    }
  }
}
