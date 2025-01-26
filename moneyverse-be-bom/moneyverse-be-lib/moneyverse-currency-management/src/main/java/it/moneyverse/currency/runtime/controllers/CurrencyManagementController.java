package it.moneyverse.currency.runtime.controllers;

import it.moneyverse.currency.model.dto.CurrencyDto;
import java.util.List;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "${spring.security.base-path")
@Validated
public class CurrencyManagementController implements CurrencyOperations {

  @Override
  public List<CurrencyDto> getCurrencies() {
    return List.of();
  }
}
