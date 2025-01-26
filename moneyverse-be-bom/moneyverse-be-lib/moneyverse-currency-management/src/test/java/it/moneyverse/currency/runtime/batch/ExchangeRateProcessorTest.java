package it.moneyverse.currency.runtime.batch;

import static org.junit.jupiter.api.Assertions.*;

import it.moneyverse.currency.model.entities.ExchangeRate;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ExchangeRateProcessorTest {

  private static final String XML_INPUT =
      """
<?xml version="1.0" encoding="UTF-8"?>
<message:StructureSpecificData xmlns:message="http://www.sdmx.org/resources/sdmxml/schemas/v2_1/message" xmlns:common="http://www.sdmx.org/resources/sdmxml/schemas/v2_1/common" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:data="http://www.sdmx.org/resources/sdmxml/schemas/v2_1/data/structurespecific" xmlns:ecb_exr1="urn:sdmx:org.sdmx.infomodel.datastructure.DataStructure=ECB:ECB_EXR1(1.0):ObsLevelDim:TIME_PERIOD" xsi:schemaLocation="http://www.sdmx.org/resources/sdmxml/schemas/v2_1/message http://data-api.ecb.europa.eu:80/vocabulary/sdmx/2_1/SDMXMessage.xsd http://www.sdmx.org/resources/sdmxml/schemas/v2_1/common http://data-api.ecb.europa.eu:80/vocabulary/sdmx/2_1/SDMXCommon.xsd urn:sdmx:org.sdmx.infomodel.datastructure.DataStructure=ECB:ECB_EXR1(1.0):ObsLevelDim:TIME_PERIOD http://data-api.ecb.europa.eu:80/service/schema/datastructure/ECB/ECB_EXR1/1.0">
    <message:Header>
        <message:ID>ac3d3562-e3d0-4a59-a8a5-8593a1190727</message:ID>
        <message:Test>false</message:Test>
        <message:Prepared>2025-01-26T15:35:54.537+01:00</message:Prepared>
        <message:Sender id="ECB"/>
        <message:Structure structureID="ECB_EXR1" dimensionAtObservation="TIME_PERIOD" namespace="urn:sdmx:org.sdmx.infomodel.datastructure.DataStructure=ECB:ECB_EXR1(1.0):ObsLevelDim:TIME_PERIOD">
            <common:Structure>
                <URN>urn:sdmx:org.sdmx.infomodel.datastructure.DataStructure=ECB:ECB_EXR1(1.0)</URN>
            </common:Structure>
        </message:Structure>
    </message:Header>
    <message:DataSet data:action="Replace" data:validFromDate="2025-01-26T15:35:54.537+01:00" data:structureRef="ECB_EXR1" data:dataScope="DataStructure" xsi:type="ecb_exr1:DataSetType">
        <Series FREQ="D" CURRENCY="GBP" CURRENCY_DENOM="EUR" EXR_TYPE="SP00" EXR_SUFFIX="A">
            <Obs TIME_PERIOD="2025-01-02" OBS_VALUE="0.83118"/>
        </Series>
        <Series FREQ="D" CURRENCY="USD" CURRENCY_DENOM="EUR" EXR_TYPE="SP00" EXR_SUFFIX="A">
            <Obs TIME_PERIOD="2025-01-02" OBS_VALUE="1.0321"/>
        </Series>
    </message:DataSet>
</message:StructureSpecificData>
""";

  private ExchangeRateProcessor processor;

  @BeforeEach
  void setUp() {
    processor = new ExchangeRateProcessor();
  }

  @Test
  void testProcess() throws Exception {
    List<ExchangeRate> result = processor.process(XML_INPUT);

    assertNotNull(result);
    assertFalse(result.isEmpty());
    assertEquals("GBP", result.getFirst().getCurrencyTo());
    assertEquals("EUR", result.getFirst().getCurrencyFrom());
    assertEquals("2025-01-02", result.getFirst().getDate().toString());
    assertEquals(BigDecimal.valueOf(0.83118), result.getFirst().getRate());
    assertEquals("USD", result.get(1).getCurrencyTo());
    assertEquals("EUR", result.get(1).getCurrencyFrom());
    assertEquals("2025-01-02", result.get(1).getDate().toString());
    assertEquals(BigDecimal.valueOf(1.0321), result.get(1).getRate());
  }
}
