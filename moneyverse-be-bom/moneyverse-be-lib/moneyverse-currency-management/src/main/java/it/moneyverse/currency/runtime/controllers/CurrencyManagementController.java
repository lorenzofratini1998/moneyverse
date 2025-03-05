package it.moneyverse.currency.runtime.controllers;

import it.moneyverse.core.model.dto.CurrencyDto;
import it.moneyverse.currency.services.CurrencyManagementService;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "${spring.security.base-path}")
@Validated
public class CurrencyManagementController implements CurrencyOperations {

  private final CurrencyManagementService currencyManagementService;

  public CurrencyManagementController(CurrencyManagementService currencyManagementService) {
    this.currencyManagementService = currencyManagementService;
  }

  @Override
  @GetMapping("/currencies")
  @ResponseStatus(HttpStatus.OK)
  public List<CurrencyDto> getCurrencies(@RequestParam Optional<Boolean> enabled) {
    return currencyManagementService.getCurrencies(enabled);
  }
}
