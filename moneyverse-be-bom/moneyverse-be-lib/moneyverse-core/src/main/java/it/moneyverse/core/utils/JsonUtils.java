package it.moneyverse.core.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.openapitools.jackson.nullable.JsonNullableModule;

public class JsonUtils {

  private static final ObjectMapper mapper =
      new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .registerModule(new JsonNullableModule())
          .registerModule(new Jdk8Module());

  public static String toJson(Object obj) {
    try {
      return mapper.writeValueAsString(obj);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  public static <T> T fromJson(String json, Class<T> clazz) {
    try {
      return mapper.readValue(json, clazz);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  private JsonUtils() {}
}
