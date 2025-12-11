package it.moneyverse.core.utils.mappers;

import it.moneyverse.core.model.dto.StyleRequestDto;
import it.moneyverse.core.model.entities.Style;

public class StyleMapper {

  public static Style toStyle(StyleRequestDto request) {
    Style style = new Style();
    style.setColor(request.color());
    style.setIcon(request.icon());
    return style;
  }

  private StyleMapper() {}
}
