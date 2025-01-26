package it.moneyverse.currency.runtime.batch;

import it.moneyverse.currency.model.batch.StructureSpecificData;
import it.moneyverse.currency.model.entities.ExchangeRate;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import java.io.StringReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateProcessor implements ItemProcessor<String, List<ExchangeRate>> {

  @Override
  public List<ExchangeRate> process(@NonNull String item) throws Exception {
    JAXBContext context = JAXBContext.newInstance(StructureSpecificData.class);
    Unmarshaller unmarshaller = context.createUnmarshaller();
    StructureSpecificData data =
        (StructureSpecificData) unmarshaller.unmarshal(new StringReader(item));
    return data.getDataSet().getSeries().stream()
        .map(
            series -> {
              DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
              ExchangeRate exchangeRate = new ExchangeRate();
              exchangeRate.setDate(LocalDate.parse(series.getObs().getTimePeriod(), formatter));
              exchangeRate.setCurrencyFrom(series.getCurrencyDenom());
              exchangeRate.setCurrencyTo(series.getCurrency());
              exchangeRate.setRate(series.getObs().getObsValue());
              return exchangeRate;
            })
        .toList();
  }
}
