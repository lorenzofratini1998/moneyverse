package it.moneyverse.test.operations.mapping;

import it.moneyverse.test.model.dto.ScriptMetadata;
import it.moneyverse.test.services.ScriptService;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EntityScriptGenerator {

  public static final String SQL_SCRIPT_FILE_NAME = "script.sql";
  private static final Logger LOGGER = LoggerFactory.getLogger(EntityScriptGenerator.class);

  private final ScriptMetadata metadata;
  private final ScriptService scriptService;

  private String script;

  public EntityScriptGenerator(ScriptMetadata metadata, ScriptService scriptService) {
    this.metadata = metadata;
    this.scriptService = scriptService;
  }

  public void execute() {
    script = generateScript();
    saveFile();
  }

  private String generateScript() {
    StringBuilder script = new StringBuilder();
    for (List<?> entities : metadata.getEntities()) {
      script.append(scriptService.createInsertScript(entities));
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
