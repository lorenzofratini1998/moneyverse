package it.moneyverse.currency.utils;

import it.moneyverse.currency.model.batch.StructureSpecificData;
import it.moneyverse.currency.model.entities.ExchangeRate;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExchangeRateUtils {

  private static final Logger LOGGER = LoggerFactory.getLogger(ExchangeRateUtils.class);

  public static final String URL =
      "https://data-api.ecb.europa.eu/service/data/EXR/D.%s.EUR.SP00.A?startPeriod=%s&endPeriod=%s&detail=dataonly&format=structurespecificdata";
  private static final String DATE_FORMAT = "yyyy-MM-dd";
  public static final LocalDate MIN_DATE = LocalDate.of(2002, 1, 2);

  public static List<ExchangeRate> parseXML(String xml) {
    if (xml == null || xml.isEmpty()) {
      return Collections.emptyList();
    }

    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATE_FORMAT);

    try {
      JAXBContext context = JAXBContext.newInstance(StructureSpecificData.class);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      StructureSpecificData data =
          (StructureSpecificData) unmarshaller.unmarshal(new StringReader(xml));
      return data.getDataSet().getSeries().stream()
          .flatMap(
              series ->
                  series.getObs().stream()
                      .map(
                          obs -> {
                            ExchangeRate exchangeRate = new ExchangeRate();
                            exchangeRate.setCurrencyFrom(series.getCurrencyDenom());
                            exchangeRate.setCurrencyTo(series.getCurrency());
                            exchangeRate.setDate(LocalDate.parse(obs.getTimePeriod(), formatter));
                            exchangeRate.setRate(obs.getObsValue());
                            return exchangeRate;
                          }))
          .toList();
    } catch (JAXBException e) {
      LOGGER.error("Failed to parse XML String: {}, ex: {}", xml, e.getMessage());
      return Collections.emptyList();
    }
  }

  private ExchangeRateUtils() {}
}
