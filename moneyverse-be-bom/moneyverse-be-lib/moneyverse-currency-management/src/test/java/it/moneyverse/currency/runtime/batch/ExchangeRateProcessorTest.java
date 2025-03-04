package it.moneyverse.currency.runtime.batch;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import it.moneyverse.currency.model.entities.ExchangeRate;
import it.moneyverse.currency.utils.ExchangeRateUtils;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExchangeRateProcessorTest {

  private MockedStatic<ExchangeRateUtils> exchangeRateUtilsMockedStatic;

  private ExchangeRateProcessor processor;

  @BeforeEach
  void setUp() {
    processor = new ExchangeRateProcessor();
    exchangeRateUtilsMockedStatic = mockStatic(ExchangeRateUtils.class);
  }

  @AfterEach
  public void tearDown() {
    exchangeRateUtilsMockedStatic.close();
  }

  @Test
  void testProcess(@Mock List<ExchangeRate> exchangeRates) {
    when(ExchangeRateUtils.parseXML(anyString())).thenReturn(exchangeRates);

    processor.process("STRING");

    exchangeRateUtilsMockedStatic.verify(() -> ExchangeRateUtils.parseXML(anyString()), times(1));
  }
}
