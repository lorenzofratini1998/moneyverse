package it.moneyverse.currency.utils;

import it.moneyverse.currency.model.batch.StructureSpecificData;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;
import java.io.StringReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLParser {

  private static final Logger LOGGER = LoggerFactory.getLogger(XMLParser.class);

  @SuppressWarnings("unchecked")
  public static <T> T unmarshalXml(String xml) {
    try {
      JAXBContext context = JAXBContext.newInstance(StructureSpecificData.class);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      return (T) unmarshaller.unmarshal(new StringReader(xml));
    } catch (JAXBException e) {
      LOGGER.error("Failed to parse XML String: {}, ex: {}", xml, e.getMessage());
      return null;
    }
  }

  private XMLParser() {}
}
