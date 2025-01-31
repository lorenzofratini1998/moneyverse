package it.moneyverse.test.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {

  private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();
  private static final String ALPHANUMERIC =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";

  private RandomUtils() {}

  public static UUID randomUUID() {
    return UUID.randomUUID();
  }

  public static Long randomLong() {
    return RANDOM.nextLong();
  }

  public static Integer randomInteger(Integer min, Integer max) {
    return RANDOM.nextInt(min, max + 1);
  }

  public static Integer randomInteger(Integer max) {
    return RANDOM.nextInt(max);
  }

  public static BigDecimal randomDecimal(Double min, Double max) {
    return BigDecimal.valueOf(RANDOM.nextDouble(min, max));
  }

  public static <T extends Enum<T>> T randomEnum(Class<T> clazz) {
    T[] constants = clazz.getEnumConstants();
    return constants[RANDOM.nextInt(constants.length)];
  }

  public static String randomString(int length) {
    if (length <= 0) {
      throw new IllegalArgumentException("length must be positive");
    }
    StringBuilder sb = new StringBuilder(length);
    for (int i = 0; i < length; i++) {
      sb.append(ALPHANUMERIC.charAt(RANDOM.nextInt(ALPHANUMERIC.length())));
    }
    return sb.toString();
  }

  public static LocalDate randomLocalDate(Integer startYear, Integer endYear) {
    int dayOfYear = RANDOM.nextInt(1, 365);
    int year = randomInteger(startYear, endYear);
    return LocalDate.ofYearDay(year, dayOfYear);
  }

  public static Boolean randomBoolean() {
    return RANDOM.nextBoolean();
  }

  public static BigDecimal randomBigDecimal() {
    return BigDecimal.valueOf(RANDOM.nextDouble());
  }
}
