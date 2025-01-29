package it.moneyverse.currency.model.batch;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class DataSet {

  @XmlElement(name = "Series")
  private List<Series> series;

  public List<Series> getSeries() {
    return series;
  }

  public void setSeries(List<Series> series) {
    this.series = series;
  }
}
