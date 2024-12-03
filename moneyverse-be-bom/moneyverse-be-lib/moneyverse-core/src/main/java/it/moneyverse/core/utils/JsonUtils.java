package it.moneyverse.core.utils;

import jakarta.json.bind.JsonbBuilder;

public class JsonUtils {

  public static String toJson(Object obj) {
    try (var jsonb = JsonbBuilder.create()) {
      return jsonb.toJson(obj);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }
}
