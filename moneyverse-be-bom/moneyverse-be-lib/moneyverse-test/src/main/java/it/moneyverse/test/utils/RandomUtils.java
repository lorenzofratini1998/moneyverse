package it.moneyverse.test.utils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
    return RANDOM.nextInt(0, max);
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

  public static LocalDate randomDate() {
    int currentYear = LocalDate.now().getYear();
    int maxDays = LocalDate.ofYearDay(currentYear, 1).lengthOfYear();
    int dayOfYear = RANDOM.nextInt(1, maxDays);
    return LocalDate.ofYearDay(currentYear, dayOfYear);
  }

  public static Boolean randomBoolean() {
    return RANDOM.nextBoolean();
  }

  public static BigDecimal randomBigDecimal() {
    return BigDecimal.valueOf(RANDOM.nextDouble());
  }

  public static String randomCurrency() {
    return randomString(3).toUpperCase();
  }

  public static <T> List<T> randomSubList(List<T> list) {
    int size = randomInteger(0, list.size());
    Collections.shuffle(list);
    return new ArrayList<>(list.subList(0, size));
  }

  public static LocalTime randomLocalTime() {
    int hours = randomInteger(0, 23);
    int minutes = randomInteger(0, 59);
    int seconds = randomInteger(1, 59);
    return LocalTime.of(hours, minutes, seconds);
  }

  public static LocalDateTime randomLocalDateTime() {
    return LocalDateTime.of(randomDate(), randomLocalTime());
  }

  public static boolean flipCoin() {
    return RANDOM.nextInt(2) == 0;
  }
}
