package it.moneyverse.test.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtils {

  private static final ThreadLocalRandom RANDOM = ThreadLocalRandom.current();

  public static UUID randomUUID() {
    return UUID.randomUUID();
  }

  public static Integer randomInteger(Integer min, Integer max) {
    return RANDOM.nextInt(min, max + 1);
  }

  public static BigDecimal randomDecimal(Double min, Double max) {
    return BigDecimal.valueOf(RANDOM.nextDouble(min, max));
  }

  public static <T extends Enum<T>> T randomEnum(Class<T> clazz) {
    T[] constants = clazz.getEnumConstants();
    return constants[RANDOM.nextInt(constants.length)];
  }

  public static LocalDate randomLocalDate(Integer startYear, Integer endYear) {
    int dayOfYear = RANDOM.nextInt(1, 365);
    int year = randomInteger(startYear, endYear);
    return LocalDate.ofYearDay(year, dayOfYear);
  }

  private RandomUtils() {
  }

}
