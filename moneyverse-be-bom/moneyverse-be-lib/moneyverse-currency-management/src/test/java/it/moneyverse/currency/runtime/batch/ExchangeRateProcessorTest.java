package it.moneyverse.currency.runtime.batch;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import it.moneyverse.currency.model.entities.ExchangeRate;
import it.moneyverse.currency.model.factories.ExchangeRateFactory;
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

  private MockedStatic<ExchangeRateFactory> exchangeRateFactoryMockedStatic;

  private ExchangeRateProcessor processor;

  @BeforeEach
  void setUp() {
    processor = new ExchangeRateProcessor();
    exchangeRateFactoryMockedStatic = mockStatic(ExchangeRateFactory.class);
  }

  @AfterEach
  public void tearDown() {
    exchangeRateFactoryMockedStatic.close();
  }

  @Test
  void testProcess(@Mock List<ExchangeRate> exchangeRates) {
    when(ExchangeRateFactory.createExchangeRates(anyString())).thenReturn(exchangeRates);

    processor.process("STRING");

    exchangeRateFactoryMockedStatic.verify(
        () -> ExchangeRateFactory.createExchangeRates(anyString()), times(1));
  }
}
