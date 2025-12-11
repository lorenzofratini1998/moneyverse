package it.moneyverse.currency.model.batch;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class Series {

  @XmlAttribute(name = "CURRENCY")
  private String currency;

  @XmlAttribute(name = "CURRENCY_DENOM")
  private String currencyDenom;

  @XmlElement(name = "Obs")
  private List<Obs> obs;

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public String getCurrencyDenom() {
    return currencyDenom;
  }

  public void setCurrencyDenom(String currencyDenom) {
    this.currencyDenom = currencyDenom;
  }

  public List<Obs> getObs() {
    return obs;
  }

  public void setObs(List<Obs> obs) {
    this.obs = obs;
  }
}
