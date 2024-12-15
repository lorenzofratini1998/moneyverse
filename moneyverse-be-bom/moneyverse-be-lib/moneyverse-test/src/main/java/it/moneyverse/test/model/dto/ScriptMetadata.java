package it.moneyverse.test.model.dto;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

public class ScriptMetadata {
  private final Path directory;
  private final List<Class<?>> entities;

  public ScriptMetadata(Path directory, Class<?>... entities) {
    this.directory = directory;
    this.entities = Arrays.asList(entities);
  }

  public Path getDirectory() {
    return directory;
  }

  public List<Class<?>> getEntities() {
    return entities;
  }
}
