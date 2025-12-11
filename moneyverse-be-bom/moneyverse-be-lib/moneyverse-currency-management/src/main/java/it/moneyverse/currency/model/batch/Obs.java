package it.moneyverse.currency.model.batch;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.FIELD)
public class Obs {

  @XmlAttribute(name = "TIME_PERIOD")
  private String timePeriod;

  @XmlAttribute(name = "OBS_VALUE")
  private String obsValue;

  public String getTimePeriod() {
    return timePeriod;
  }

  public void setTimePeriod(String timePeriod) {
    this.timePeriod = timePeriod;
  }

  public String getObsValue() {
    return obsValue;
  }

  public void setObsValue(String obsValue) {
    this.obsValue = obsValue;
  }
}
