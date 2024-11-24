package it.moneyverse.account.utils;

import it.moneyverse.account.model.dto.AccountRequestDto;
import jakarta.json.bind.JsonbBuilder;

public class TestUtils {

  public static String toJson(AccountRequestDto accountRequestDto) {
    try (var jsonb = JsonbBuilder.create()) {
      return jsonb.toJson(accountRequestDto);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

  private TestUtils() {
  }
}
