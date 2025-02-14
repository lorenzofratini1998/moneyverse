package it.moneyverse.budget.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import it.moneyverse.budget.model.dto.CategoryRequestDto;
import it.moneyverse.budget.model.dto.CategoryUpdateRequestDto;
import it.moneyverse.budget.model.entities.Category;
import it.moneyverse.budget.model.event.CategoryDeletionEvent;
import it.moneyverse.budget.model.repositories.CategoryRepository;
import it.moneyverse.budget.utils.mapper.CategoryMapper;
import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.dto.CategoryDto;
import it.moneyverse.core.services.MessageProducer;
import it.moneyverse.core.services.UserServiceClient;
import it.moneyverse.test.utils.RandomUtils;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openapitools.jackson.nullable.JsonNullable;
import org.springframework.kafka.support.SendResult;

/** Unit test for {@link CategoryManagementService} */
@ExtendWith(MockitoExtension.class)
class CategoryManagementServiceTest {

  @InjectMocks private CategoryManagementService categoryManagementService;

  @Mock private CategoryRepository categoryRepository;
  @Mock private UserServiceClient userServiceClient;
  @Mock private MessageProducer<UUID, String> messageProducer;
  private MockedStatic<CategoryMapper> mapper;

  @BeforeEach
  public void setup() {
    mapper = mockStatic(CategoryMapper.class);
  }

  @AfterEach
  public void tearDown() {
    mapper.close();
  }

  @Test
  void givenCategoryRequest_WhenCreateCategory_ThenReturnCreatedCategory(
      @Mock Category category, @Mock CategoryDto categoryDto) {
    final UUID userId = RandomUtils.randomUUID();
    CategoryRequestDto request = createCategoryRequest(userId);

    when(categoryRepository.existsByUserIdAndCategoryName(userId, request.categoryName()))
        .thenReturn(false);
    mapper.when(() -> CategoryMapper.toCategory(request)).thenReturn(category);
    when(categoryRepository.save(any(Category.class))).thenReturn(category);
    mapper.when(() -> CategoryMapper.toCategoryDto(category)).thenReturn(categoryDto);

    categoryDto = categoryManagementService.createCategory(request);

    assertNotNull(categoryDto);
    verify(categoryRepository, times(1))
        .existsByUserIdAndCategoryName(userId, request.categoryName());
    mapper.verify(() -> CategoryMapper.toCategory(request), times(1));
    verify(categoryRepository, times(1)).save(any(Category.class));
    mapper.verify(() -> CategoryMapper.toCategoryDto(category), times(1));
  }

  @Test
  void givenCategoryRequest_WhenCreateCategory_ThenCategoryAlreadyExists() {
    final UUID userId = RandomUtils.randomUUID();
    CategoryRequestDto request = createCategoryRequest(userId);

    when(categoryRepository.existsByUserIdAndCategoryName(userId, request.categoryName()))
        .thenReturn(true);

    assertThrows(
        ResourceAlreadyExistsException.class,
        () -> categoryManagementService.createCategory(request));

    verify(categoryRepository, never()).save(any(Category.class));
    verify(categoryRepository, times(1))
        .existsByUserIdAndCategoryName(userId, request.categoryName());
  }

  private CategoryRequestDto createCategoryRequest(UUID userId) {
    return new CategoryRequestDto(
        userId, null, RandomUtils.randomString(15), RandomUtils.randomString(15));
  }

  @Test
  void givenCategoryId_WhenGetCategory_ThenReturnCategory(
      @Mock Category category, @Mock CategoryDto categoryDto) {
    UUID categoryId = RandomUtils.randomUUID();

    when(categoryRepository.findById(categoryId)).thenReturn(Optional.ofNullable(category));
    mapper.when(() -> CategoryMapper.toCategoryDto(category)).thenReturn(categoryDto);

    categoryDto = categoryManagementService.getCategory(categoryId);

    assertNotNull(categoryDto);
    verify(categoryRepository, times(1)).findById(categoryId);
    mapper.verify(() -> CategoryMapper.toCategoryDto(category), times(1));
  }

  @Test
  void givenCategoryId_WhenGetCategory_ThenCategoryNotFound() {
    UUID categoryId = RandomUtils.randomUUID();

    when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class, () -> categoryManagementService.getCategory(categoryId));

    verify(categoryRepository, times(1)).findById(categoryId);
    mapper.verify(() -> CategoryMapper.toCategoryDto(any(Category.class)), never());
  }

  @Test
  void givenCategoryId_WhenUpdateCategory_ThenReturnCategoryDto(
      @Mock Category category, @Mock CategoryDto categoryDto) {
    UUID categoryId = RandomUtils.randomUUID();
    CategoryUpdateRequestDto request = createCategoryUpdateRequest();

    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
    when(categoryRepository.existsByUserIdAndCategoryName(
            category.getUserId(), request.categoryName()))
        .thenReturn(false);
    mapper.when(() -> CategoryMapper.partialUpdate(category, request)).thenReturn(category);
    when(categoryRepository.save(category)).thenReturn(category);
    mapper.when(() -> CategoryMapper.toCategoryDto(category)).thenReturn(categoryDto);

    categoryDto = categoryManagementService.updateCategory(categoryId, request);

    assertNotNull(categoryDto);
    verify(categoryRepository, times(1)).findById(categoryId);
    verify(categoryRepository, times(1))
        .existsByUserIdAndCategoryName(category.getUserId(), request.categoryName());
    mapper.verify(() -> CategoryMapper.partialUpdate(category, request), times(1));
    verify(categoryRepository, times(1)).save(category);
    mapper.verify(() -> CategoryMapper.toCategoryDto(category), times(1));
  }

  @Test
  void givenCategoryId_WhenUpdateCategory_ThenCategoryNotFound() {
    UUID categoryId = RandomUtils.randomUUID();
    CategoryUpdateRequestDto request = createCategoryUpdateRequest();

    when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> categoryManagementService.updateCategory(categoryId, request));

    verify(categoryRepository, times(1)).findById(categoryId);
    verify(categoryRepository, never())
        .existsByUserIdAndCategoryName(any(UUID.class), any(String.class));

    mapper.verify(
        () ->
            CategoryMapper.partialUpdate(any(Category.class), any(CategoryUpdateRequestDto.class)),
        never());
    verify(categoryRepository, never()).save(any(Category.class));
    mapper.verify(() -> CategoryMapper.toCategoryDto(any(Category.class)), never());
  }

  @Test
  void givenCategoryId_WhenUpdateCategory_ThenCategoryAlreadyExists(@Mock Category category) {
    UUID categoryId = RandomUtils.randomUUID();
    CategoryUpdateRequestDto request = createCategoryUpdateRequest();

    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
    when(categoryRepository.existsByUserIdAndCategoryName(
            category.getUserId(), request.categoryName()))
        .thenReturn(true);
    assertThrows(
        ResourceAlreadyExistsException.class,
        () -> categoryManagementService.updateCategory(categoryId, request));

    verify(categoryRepository, times(1)).findById(categoryId);
    verify(categoryRepository, times(1))
        .existsByUserIdAndCategoryName(category.getUserId(), request.categoryName());

    mapper.verify(
        () ->
            CategoryMapper.partialUpdate(any(Category.class), any(CategoryUpdateRequestDto.class)),
        never());
    verify(categoryRepository, never()).save(any(Category.class));
    mapper.verify(() -> CategoryMapper.toCategoryDto(any(Category.class)), never());
  }

  private CategoryUpdateRequestDto createCategoryUpdateRequest() {
    return new CategoryUpdateRequestDto(
        RandomUtils.randomString(15), RandomUtils.randomString(15), JsonNullable.undefined());
  }

  @Test
  void givenCategoryId_WhenDeleteCategory_ThenDeleteCategory(
      @Mock Category category, @Mock CompletableFuture<SendResult<UUID, String>> future) {
    UUID categoryId = RandomUtils.randomUUID();

    when(categoryRepository.findById(categoryId)).thenReturn(Optional.of(category));
    when(messageProducer.send(any(CategoryDeletionEvent.class), any(String.class)))
        .thenReturn(future);

    categoryManagementService.deleteCategory(categoryId);

    verify(categoryRepository, times(1)).findById(categoryId);
    verify(messageProducer, times(1)).send(any(CategoryDeletionEvent.class), any(String.class));
  }

  @Test
  void givenCategoryId_WhenDeleteCategory_ThenCategoryNotFound() {
    UUID categoryId = RandomUtils.randomUUID();

    when(categoryRepository.findById(categoryId)).thenReturn(Optional.empty());

    assertThrows(
        ResourceNotFoundException.class,
        () -> categoryManagementService.deleteCategory(categoryId));

    verify(categoryRepository, times(1)).findById(categoryId);
    verify(messageProducer, never()).send(any(CategoryDeletionEvent.class), any(String.class));
  }
}
