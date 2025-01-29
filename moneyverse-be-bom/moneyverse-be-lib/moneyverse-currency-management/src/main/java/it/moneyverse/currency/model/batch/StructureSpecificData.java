package it.moneyverse.currency.model.batch;

import jakarta.xml.bind.annotation.*;

@XmlRootElement(
    name = "StructureSpecificData",
    namespace = "http://www.sdmx.org/resources/sdmxml/schemas/v2_1/message")
@XmlAccessorType(XmlAccessType.FIELD)
public class StructureSpecificData {

  @XmlElement(
      name = "DataSet",
      namespace = "http://www.sdmx.org/resources/sdmxml/schemas/v2_1/message")
  private DataSet dataSet;

  public DataSet getDataSet() {
    return dataSet;
  }

  public void setDataSet(DataSet dataSet) {
    this.dataSet = dataSet;
  }
}
