package it.moneyverse.currency.runtime.controllers;

import it.moneyverse.currency.model.dto.CurrencyDto;
import it.moneyverse.currency.services.CurrencyManagementService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
  public List<CurrencyDto> getCurrencies() {
    return currencyManagementService.getCurrencies();
  }
}
