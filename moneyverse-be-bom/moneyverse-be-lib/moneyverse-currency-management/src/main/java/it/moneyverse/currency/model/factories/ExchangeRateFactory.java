package it.moneyverse.currency.model.factories;

import it.moneyverse.currency.model.batch.Obs;
import it.moneyverse.currency.model.batch.Series;
import it.moneyverse.currency.model.batch.StructureSpecificData;
import it.moneyverse.currency.model.entities.ExchangeRate;
import it.moneyverse.currency.utils.XMLParser;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ExchangeRateFactory {

  private static final String DATE_FORMAT = "yyyy-MM-dd";

  public static List<ExchangeRate> createExchangeRates(String xml) {
    if (xml == null || xml.isEmpty()) {
      return Collections.emptyList();
    }
    StructureSpecificData data = XMLParser.unmarshalXml(xml);
    if (data == null) {
      return Collections.emptyList();
    }
    Map<String, BigDecimal> lastValidRates = new HashMap<>();
    return data.getDataSet().getSeries().stream()
        .flatMap(series -> processSeries(series, lastValidRates).stream())
        .toList();
  }

  private static List<ExchangeRate> processSeries(
      Series series, Map<String, BigDecimal> lastValidRates) {
    List<Obs> sortedObs = sortObservations(series.getObs());

    return sortedObs.stream()
        .map(obs -> createExchangeRate(series, obs, lastValidRates))
        .filter(exchangeRate -> exchangeRate.getRate() != null)
        .toList();
  }

  private static List<Obs> sortObservations(List<Obs> obsList) {
    return obsList.stream()
        .sorted(
            Comparator.comparing(
                obs ->
                    LocalDate.parse(obs.getTimePeriod(), DateTimeFormatter.ofPattern(DATE_FORMAT))))
        .toList();
  }

  private static ExchangeRate createExchangeRate(
      Series series, Obs obs, Map<String, BigDecimal> lastValidRates) {
    String currencyPair = series.getCurrencyDenom() + "_" + series.getCurrency();
    BigDecimal rate = getValidRate(obs.getObsValue(), currencyPair, lastValidRates);

    ExchangeRate exchangeRate = new ExchangeRate();
    exchangeRate.setCurrencyFrom(series.getCurrencyDenom());
    exchangeRate.setCurrencyTo(series.getCurrency());
    exchangeRate.setDate(
        LocalDate.parse(obs.getTimePeriod(), DateTimeFormatter.ofPattern(DATE_FORMAT)));
    exchangeRate.setRate(rate);

    return exchangeRate;
  }

  private static BigDecimal getValidRate(
      String obsValue, String currencyPair, Map<String, BigDecimal> lastValidRates) {
    if ("NaN".equals(obsValue)) {
      return lastValidRates.getOrDefault(currencyPair, null);
    }
    BigDecimal rate = new BigDecimal(obsValue);
    lastValidRates.put(currencyPair, rate);
    return rate;
  }

  private ExchangeRateFactory() {}
}
