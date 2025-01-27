package it.moneyverse.currency.runtime.controllers;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.moneyverse.currency.model.dto.CurrencyDto;
import it.moneyverse.currency.services.CurrencyManagementService;
import it.moneyverse.test.runtime.processor.MockAdminRequestPostProcessor;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(
    controllers = {CurrencyManagementController.class},
    excludeAutoConfiguration = {DataSourceAutoConfiguration.class})
@ExtendWith(MockitoExtension.class)
class CurrencyManagementControllerTest {

  @Value("${spring.security.base-path}")
  String basePath;

  @Autowired private MockMvc mockMvc;
  @MockitoBean private CurrencyManagementService currencyManagementService;

  @Test
  void testGetCurrencies_Success(@Mock CurrencyDto dto) throws Exception {
    when(currencyManagementService.getCurrencies()).thenReturn(List.of(dto));

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/currencies")
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isOk());
  }

  @Test
  void testGetCurrencies_Unauthorized() throws Exception {

    mockMvc
        .perform(MockMvcRequestBuilders.get(basePath + "/currencies"))
        .andExpect(status().isUnauthorized());
  }
}
