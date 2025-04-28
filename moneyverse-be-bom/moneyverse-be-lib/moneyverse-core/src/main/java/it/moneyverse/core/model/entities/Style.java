package it.moneyverse.core.model.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class Style {

  @Column(name = "BACKGROUND_COLOR")
  private String backgroundColor;

  @Column(name = "TEXT_COLOR")
  private String textColor;

  @Column(name = "ICON")
  private String icon;

  public String getBackgroundColor() {
    return backgroundColor;
  }

  public void setBackgroundColor(String backgroundColor) {
    this.backgroundColor = backgroundColor;
  }

  public String getTextColor() {
    return textColor;
  }

  public void setTextColor(String textColor) {
    this.textColor = textColor;
  }

  public String getIcon() {
    return icon;
  }

  public void setIcon(String icon) {
    this.icon = icon;
  }
}
