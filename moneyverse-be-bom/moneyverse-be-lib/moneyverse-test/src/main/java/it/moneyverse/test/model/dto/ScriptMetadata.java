package it.moneyverse.test.model.dto;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class ScriptMetadata {
  private final Path directory;
  private final List<List<?>> entities;

  public ScriptMetadata(Path directory, List<?>... entities) {
    this.directory = directory;
    this.entities = Arrays.asList(entities);
  }

  public Path getDirectory() {
    return directory;
  }

  public List<List<?>> getEntities() {
    return entities;
  }
}
