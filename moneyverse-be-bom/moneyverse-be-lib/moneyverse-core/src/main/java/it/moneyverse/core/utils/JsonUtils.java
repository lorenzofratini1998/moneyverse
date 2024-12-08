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

  public static <T> T fromJson(String json, Class<T> clazz) {
    try (var jsonb = JsonbBuilder.create()) {
      return jsonb.fromJson(json, clazz);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }
}
