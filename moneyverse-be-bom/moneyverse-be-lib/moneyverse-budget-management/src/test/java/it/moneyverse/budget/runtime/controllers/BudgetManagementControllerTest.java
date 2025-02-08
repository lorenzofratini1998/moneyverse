package it.moneyverse.budget.runtime.controllers;

import static it.moneyverse.budget.utils.BudgetTestUtils.createBudgetRequest;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import it.moneyverse.budget.model.dto.*;
import it.moneyverse.budget.services.BudgetManagementService;
import it.moneyverse.budget.services.CategoryManagementService;
import it.moneyverse.core.boot.DatasourceAutoConfiguration;
import it.moneyverse.core.boot.KafkaAutoConfiguration;
import it.moneyverse.core.boot.UserServiceGrpcClientAutoConfiguration;
import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.test.runtime.processor.MockAdminRequestPostProcessor;
import it.moneyverse.test.runtime.processor.MockUserRequestPostProcessor;
import it.moneyverse.test.utils.RandomUtils;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;
import org.openapitools.jackson.nullable.JsonNullableModule;
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
  @MockitoBean private CategoryManagementService categoryService;
  @MockitoBean private BudgetManagementService budgetService;
  private final ObjectMapper objectMapper =
      new ObjectMapper()
          .registerModule(new JavaTimeModule())
          .registerModule(new JsonNullableModule());

  @Test
  void testCreateCategory_Success(@Mock CategoryDto response) throws Exception {
    CategoryRequestDto request = createCategoryRequest();
    when(categoryService.createCategory(request)).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/categories")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockUserRequestPostProcessor.mockUser(request.userId())))
        .andExpect(status().isCreated());
  }

  @Test
  void testCreateCategory_Forbidden() throws Exception {
    CategoryRequestDto request = createCategoryRequest();

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/categories")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
        .andExpect(status().isForbidden());
  }

  @ParameterizedTest
  @MethodSource("invalidCategoryRequestProvider")
  void testCreateCategory_BadRequest(Supplier<CategoryRequestDto> requestSupplier)
      throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/categories")
                .content(requestSupplier.get().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isBadRequest());
    verify(categoryService, never()).createCategory(requestSupplier.get());
  }

  private static Stream<Supplier<CategoryRequestDto>> invalidCategoryRequestProvider() {
    return Stream.of(
        BudgetManagementControllerTest::createRequestWithNullUserId,
        BudgetManagementControllerTest::createRequestWithNullCategoryName);
  }

  private static CategoryRequestDto createRequestWithNullUserId() {
    return new CategoryRequestDto(
        null, null, RandomUtils.randomString(15), RandomUtils.randomString(15));
  }

  private static CategoryRequestDto createRequestWithNullCategoryName() {
    return new CategoryRequestDto(
        RandomUtils.randomUUID(), null, null, RandomUtils.randomString(15));
  }

  @Test
  void testCreateCategory_CategoryAlreadyExists() throws Exception {
    CategoryRequestDto request =
        new CategoryRequestDto(
            RandomUtils.randomUUID(),
            null,
            RandomUtils.randomString(15),
            RandomUtils.randomString(15));
    when(categoryService.createCategory(request)).thenThrow(ResourceAlreadyExistsException.class);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/categories")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockUserRequestPostProcessor.mockUser(request.userId())))
        .andExpect(status().isConflict());
  }

  @Test
  void testCreateCategory_UserNotFound() throws Exception {
    CategoryRequestDto request = createCategoryRequest();
    when(categoryService.createCategory(request)).thenThrow(ResourceNotFoundException.class);
    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/categories")
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockUserRequestPostProcessor.mockUser(request.userId())))
        .andExpect(status().isNotFound());
  }

  private CategoryRequestDto createCategoryRequest() {
    return new CategoryRequestDto(
        RandomUtils.randomUUID(), null, RandomUtils.randomString(15), RandomUtils.randomString(15));
  }

  @Test
  void testGetCategories_ByUser_Success(
      @Mock PageCriteria criteria, @Mock List<CategoryDto> response) throws Exception {
    UUID userId = RandomUtils.randomUUID();
    when(categoryService.getCategoriesByUserId(userId, criteria)).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/categories/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockUserRequestPostProcessor.mockUser(userId)))
        .andExpect(status().isOk());
  }

  @Test
  void testGetCategories_ByUser_Unauthorized() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/categories")
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testGetCategory_Success(@Mock CategoryDto response) throws Exception {
    UUID categoryId = RandomUtils.randomUUID();
    UUID userId = RandomUtils.randomUUID();
    when(categoryService.getCategory(categoryId)).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/categories/" + categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockUserRequestPostProcessor.mockUser(userId)))
        .andExpect(status().isOk());
  }

  @Test
  void testGetCategory_Unauthorized() throws Exception {
    UUID categoryId = RandomUtils.randomUUID();
    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/categories/" + categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
        .andExpect(status().isUnauthorized());
  }

  @Test
  void testGetCategory_NotFound() throws Exception {
    UUID categoryId = RandomUtils.randomUUID();
    when(categoryService.getCategory(categoryId)).thenThrow(ResourceNotFoundException.class);

    mockMvc
        .perform(
            MockMvcRequestBuilders.get(basePath + "/categories/" + categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isNotFound());
  }

  @Test
  void testUpdateCategorySuccess(@Mock CategoryDto response) throws Exception {
    UUID categoryId = RandomUtils.randomUUID();
    CategoryUpdateRequestDto request = createCategoryUpdateRequest();
    when(categoryService.updateCategory(categoryId, request)).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(basePath + "/categories/" + categoryId)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isOk());
  }

  @Test
  void testUpdateCategory_Forbidden() throws Exception {
    UUID categoryId = RandomUtils.randomUUID();
    CategoryUpdateRequestDto request = createCategoryUpdateRequest();

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(basePath + "/categories/" + categoryId)
                .content(request.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
        .andExpect(status().isForbidden());
  }

  @Disabled
  @Test
  void testUpdateCategory_NotFound() throws Exception {
    UUID categoryId = RandomUtils.randomUUID();
    CategoryUpdateRequestDto request = createCategoryUpdateRequest();
    when(categoryService.updateCategory(categoryId, request))
        .thenThrow(ResourceNotFoundException.class);

    mockMvc
        .perform(
            MockMvcRequestBuilders.put(basePath + "/categories/" + categoryId)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isNotFound());
  }

  private CategoryUpdateRequestDto createCategoryUpdateRequest() {
    return new CategoryUpdateRequestDto(
        RandomUtils.randomString(15), RandomUtils.randomString(15), JsonNullable.undefined());
  }

  @Test
  void testDeleteCategory_Success() throws Exception {
    UUID categoryId = RandomUtils.randomUUID();

    Mockito.doNothing().when(categoryService).deleteCategory(categoryId);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(basePath + "/categories/" + categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isNoContent());
  }

  @Test
  void testDeleteCategory_NotFound() throws Exception {
    UUID categoryId = RandomUtils.randomUUID();
    Mockito.doThrow(ResourceNotFoundException.class)
        .when(categoryService)
        .deleteCategory(categoryId);

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(basePath + "/categories/" + categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(MockAdminRequestPostProcessor.mockAdmin()))
        .andExpect(status().isNotFound());
  }

  @Test
  void testDeleteCategory_Forbidden() throws Exception {
    UUID categoryId = RandomUtils.randomUUID();

    mockMvc
        .perform(
            MockMvcRequestBuilders.delete(basePath + "/categories/" + categoryId)
                .contentType(MediaType.APPLICATION_JSON)
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
        .andExpect(status().isForbidden());
  }

  @Test
  void testCreateBudget_Success(@Mock BudgetDto response) throws Exception {
    BudgetRequestDto request = createBudgetRequest();
    when(budgetService.createBudget(request)).thenReturn(response);

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString())
                .with(MockUserRequestPostProcessor.mockUser(RandomUtils.randomUUID())))
        .andExpect(status().isCreated());
  }

  @Test
  void testCreateBudget_Forbidden() throws Exception {
    BudgetRequestDto request = createBudgetRequest();

    mockMvc
        .perform(
            MockMvcRequestBuilders.post(basePath + "/budgets")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request.toString())
                .with(SecurityMockMvcRequestPostProcessors.anonymous()))
        .andExpect(status().isForbidden());
  }
}
