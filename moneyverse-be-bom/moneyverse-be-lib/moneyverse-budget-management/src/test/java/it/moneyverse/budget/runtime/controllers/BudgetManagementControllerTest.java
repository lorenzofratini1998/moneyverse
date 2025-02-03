package it.moneyverse.budget.runtime.controllers;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import it.moneyverse.budget.model.dto.BudgetCriteria;
import it.moneyverse.budget.model.dto.BudgetDto;
import it.moneyverse.budget.model.dto.BudgetRequestDto;
import it.moneyverse.budget.model.dto.BudgetUpdateRequestDto;
import it.moneyverse.budget.services.BudgetManagementService;
import it.moneyverse.core.boot.DatasourceAutoConfiguration;
import it.moneyverse.core.boot.KafkaAutoConfiguration;
import it.moneyverse.core.boot.UserServiceGrpcClientAutoConfiguration;
import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.test.runtime.processor.MockAdminRequestPostProcessor;
import it.moneyverse.test.utils.RandomUtils;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@WebMvcTest(
    controllers = BudgetManagementController.class,
    excludeAutoConfiguration = {
      DatasourceAutoConfiguration.class,
      UserServiceGrpcClientAutoConfiguration.class,
      KafkaAutoConfiguration.class
    })
@ExtendWith(MockitoExtension.class)
class BudgetManagementControllerTest {

  @Value("${spring.security.base-path}")
  protected String basePath;

  @Autowired private MockMvc mockMvc;
  @MockitoBean private BudgetManagementService budgetService;

  @Test
  void testCreateBudget_Success(@Mock BudgetDto response) throws Exception {
    BudgetRequestDto request = createBudgetRequest();
    when(budgetService.createBudget(request)).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/budgets")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isCreated());
  }

  @Test
  void testCreateBudget_Forbidden() throws Exception {
    BudgetRequestDto request = createBudgetRequest();

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/budgets")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @MethodSource("invalidBudgetRequestProvider")
  void testCreateBudget_BadRequest(Supplier<BudgetRequestDto> requestSupplier) throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/budgets")
                .content(requestSupplier.get().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isBadRequest());
    verify(budgetService, never()).createBudget(requestSupplier.get());
  }

  private static Stream<Supplier<BudgetRequestDto>> invalidBudgetRequestProvider() {
    return Stream.of(
        BudgetManagementControllerTest::createRequestWithNullUserId,
        BudgetManagementControllerTest::createRequestWithNullBudgetName,
        BudgetManagementControllerTest::createRequestWithNullCurrency);
  }

  private static BudgetRequestDto createRequestWithNullUserId() {
    return new BudgetRequestDto(
        null,
        RandomUtils.randomString(15),
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomString(3).toUpperCase());
  }

  private static BudgetRequestDto createRequestWithNullBudgetName() {
    return new BudgetRequestDto(
        RandomUtils.randomUUID(),
        null,
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomString(3).toUpperCase());
  }

  private static BudgetRequestDto createRequestWithNullCurrency() {
    return new BudgetRequestDto(
        RandomUtils.randomUUID(),
        RandomUtils.randomString(15),
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomBigDecimal(),
        null);
  }

  @Test
  void testBudgetCreation_BudgetAlreadyExists() throws Exception {
    BudgetRequestDto request =
        new BudgetRequestDto(
            RandomUtils.randomUUID(),
            RandomUtils.randomString(15),
            RandomUtils.randomString(15),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomBigDecimal(),
            RandomUtils.randomString(3).toUpperCase());
    when(budgetService.createBudget(request)).thenThrow(ResourceAlreadyExistsException.class);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/budgets")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isConflict());
  }

  @Test
  void testBudgetCreation_UserNotFound() throws Exception {
    BudgetRequestDto request = createBudgetRequest();
    when(budgetService.createBudget(request)).thenThrow(ResourceNotFoundException.class);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/budgets")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isNotFound());
  }

  private BudgetRequestDto createBudgetRequest() {
    return new BudgetRequestDto(
        RandomUtils.randomUUID(),
        RandomUtils.randomString(15),
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomString(3).toUpperCase());
  }

  @Test
  void testGetBudgets_Success(@Mock BudgetCriteria criteria, @Mock List<BudgetDto> response)
      throws Exception {
    when(budgetService.getBudgets(criteria)).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isOk());
  }

  @Test
  void testGetBudgets_Unauthorized() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testGetBudget_Success(@Mock BudgetDto response) throws Exception {
    UUID budgetId = RandomUtils.randomUUID();
    when(budgetService.getBudget(budgetId)).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/budgets/" + budgetId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isOk());
  }

  @Test
  void testGetBudget_Unauthorized() throws Exception {
    UUID budgetId = RandomUtils.randomUUID();
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/budgets/" + budgetId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testGetBudget_NotFound() throws Exception {
    UUID budgetId = RandomUtils.randomUUID();
    when(budgetService.getBudget(budgetId)).thenThrow(ResourceNotFoundException.class);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/budgets/" + budgetId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isNotFound());
  }

  @Test
  void testUpdateBudgetSuccess(@Mock BudgetDto response) throws Exception {
    UUID budgetId = RandomUtils.randomUUID();
    BudgetUpdateRequestDto request = createBudgetUpdateRequest();
    when(budgetService.updateBudget(budgetId, request)).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(basePath + "/budgets/" + budgetId)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isOk());
  }

  @Test
  void testUpdateBudget_Forbidden() throws Exception {
    UUID budgetId = RandomUtils.randomUUID();
    BudgetUpdateRequestDto request = createBudgetUpdateRequest();

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(basePath + "/budgets/" + budgetId)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
        .andExpect(status().isForbidden());
  }

  @Test
  void testUpdateBudget_NotFound() throws Exception {
    UUID budgetId = RandomUtils.randomUUID();
    BudgetUpdateRequestDto request = createBudgetUpdateRequest();
    when(budgetService.updateBudget(budgetId, request)).thenThrow(ResourceNotFoundException.class);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(basePath + "/budgets/" + budgetId)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isNotFound());
  }

  private BudgetUpdateRequestDto createBudgetUpdateRequest() {
    return new BudgetUpdateRequestDto(
        RandomUtils.randomString(15),
        RandomUtils.randomString(15),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomBigDecimal(),
        RandomUtils.randomString(3).toUpperCase());
  }

  @Test
  void testDeleteBudget_Success() throws Exception {
    UUID budgetId = RandomUtils.randomUUID();

    Mockito.doNothing().when(budgetService).deleteBudget(budgetId);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(basePath + "/budgets/" + budgetId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isNoContent());
  }

  @Test
  void testDeleteBudget_NotFound() throws Exception {
    UUID budgetId = RandomUtils.randomUUID();
    Mockito.doThrow(ResourceNotFoundException.class).when(budgetService).deleteBudget(budgetId);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(basePath + "/budgets/" + budgetId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteBudget_Forbidden() throws Exception {
    UUID budgetId = RandomUtils.randomUUID();

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(basePath + "/budgets/" + budgetId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
        .andExpect(status().isForbidden());
  }
}
