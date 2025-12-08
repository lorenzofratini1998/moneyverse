package it.moneyverse.budget.services;

import it.moneyverse.budget.enums.CategorySseEventEnum;
import it.moneyverse.budget.model.dto.CategoryRequestDto;
import it.moneyverse.budget.model.dto.CategoryUpdateRequestDto;
import it.moneyverse.budget.model.entities.Category;
import it.moneyverse.budget.model.entities.Category_;
import it.moneyverse.budget.model.repositories.CategoryRepository;
import it.moneyverse.budget.model.repositories.DefaultCategoryRepository;
import it.moneyverse.budget.runtime.messages.CategoryEventPublisher;
import it.moneyverse.budget.utils.mapper.CategoryMapper;
import it.moneyverse.core.enums.EventTypeEnum;
import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.dto.CategoryDto;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.services.SecurityService;
import it.moneyverse.core.services.SseEventService;
import it.moneyverse.core.services.UserServiceClient;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryManagementService implements CategoryService {

  private static final Logger LOGGER = LoggerFactory.getLogger(CategoryManagementService.class);

  private final CategoryRepository categoryRepository;
  private final DefaultCategoryRepository defaultCategoryRepository;
  private final UserServiceClient userServiceClient;
  private final CategoryEventPublisher categoryEventPublisher;
  private final SecurityService securityService;
  private final SseEventService eventService;

  public CategoryManagementService(
      CategoryRepository categoryRepository,
      DefaultCategoryRepository defaultCategoryRepository,
      UserServiceClient userServiceClient,
      CategoryEventPublisher categoryEventPublisher,
      SecurityService securityService,
      SseEventService eventService) {
    this.categoryRepository = categoryRepository;
    this.defaultCategoryRepository = defaultCategoryRepository;
    this.userServiceClient = userServiceClient;
    this.categoryEventPublisher = categoryEventPublisher;
    this.securityService = securityService;
    this.eventService = eventService;
  }

  @Override
  @Transactional
  public CategoryDto createCategory(CategoryRequestDto request) {
    checkIfCategoryAlreadyExists(request.userId(), request.categoryName());
    Category parentCategory = null;
    if (request.parentId() != null) {
      parentCategory = findCategoryById(request.parentId());
    }
    LOGGER.info("Creating category '{}' for user '{}'", request.categoryName(), request.userId());
    Category category =
        parentCategory == null
            ? CategoryMapper.toCategory(request)
            : CategoryMapper.toCategory(request, parentCategory);
    CategoryDto result = CategoryMapper.toCategoryDto(categoryRepository.save(category));
    LOGGER.info("Created category '{}' for user '{}'", result, request.userId());
    eventService.publishEvent(
        securityService.getAuthenticatedUserId(),
        CategorySseEventEnum.CATEGORY_CREATED.name(),
        result);
    return result;
  }

  @Override
  @Transactional(readOnly = true)
  public List<CategoryDto> getCategories(Boolean defaultCategories) {
    return CategoryMapper.mapDefaultCategories(defaultCategoryRepository.findAll());
  }

  @Override
  @Transactional(readOnly = true)
  public List<CategoryDto> getCategoriesByUserId(UUID userId, PageCriteria pageCriteria) {
    LOGGER.info("Fetching categories for user: '{}'", userId);
    int offset = pageCriteria.getOffset() != null ? pageCriteria.getOffset() : 0;
    int limit = pageCriteria.getLimit() != null ? pageCriteria.getLimit() : Integer.MAX_VALUE;
    Pageable pageRequest =
        PageRequest.of(offset, limit, Sort.by(Category_.CATEGORY_NAME).ascending());
    return CategoryMapper.toCategoryDto(
        categoryRepository.findCategoriesByUserId(userId, pageRequest));
  }

  @Override
  @Transactional
  public void createUserDefaultCategories(UUID userId) {
    LOGGER.info("Creating default categories for user: '{}'", userId);
    categoryRepository.saveAll(
        defaultCategoryRepository.findAll().stream()
            .map(defaultCategory -> CategoryMapper.toCategory(userId, defaultCategory))
            .toList());
    LOGGER.info("Created default categories for user: '{}'", userId);
    eventService.publishEvent(
        securityService.getAuthenticatedUserId(),
        CategorySseEventEnum.CATEGORY_CREATED.name(),
        userId);
  }

  @Override
  @Transactional(readOnly = true)
  public List<CategoryDto> getCategoryTreeByUserId(UUID userId) {
    LOGGER.info("Fetching category tree for user: '{}'", userId);
    return CategoryMapper.toCategoryDto(categoryRepository.findCategoryTreeByUserId(userId));
  }

  @Override
  @Transactional(readOnly = true)
  public CategoryDto getCategory(UUID categoryId) {
    return CategoryMapper.toCategoryDto(findCategoryById(categoryId));
  }

  @Override
  @Transactional
  public CategoryDto updateCategory(UUID categoryId, CategoryUpdateRequestDto request) {
    Category category = findCategoryById(categoryId);
    if (request.categoryName() != null) {
      checkIfCategoryAlreadyExists(category.getUserId(), request.categoryName(), categoryId);
    }
    category =
        request.parentId() != null
            ? updateCategoryWithParent(category, request)
            : CategoryMapper.partialUpdate(category, request);
    CategoryDto result = CategoryMapper.toCategoryDto(categoryRepository.save(category));
    LOGGER.info("Updated category '{}' for user '{}'", categoryId, category.getUserId());
    eventService.publishEvent(
        securityService.getAuthenticatedUserId(),
        CategorySseEventEnum.CATEGORY_UPDATED.name(),
        result);
    return result;
  }

  private Category updateCategoryWithParent(Category category, CategoryUpdateRequestDto request) {
    if (request.parentId() == null) {
      return CategoryMapper.partialUpdate(category, request, null);
    }
    UUID parentId = request.parentId();
    if (category.getCategoryId().equals(parentId)) {
      throw new IllegalArgumentException("Category cannot be its own parent");
    }
    Category parentCategory = findCategoryById(parentId);
    return CategoryMapper.partialUpdate(category, request, parentCategory);
  }

  private void checkIfCategoryAlreadyExists(UUID userId, String categoryName) {
    if (Boolean.TRUE.equals(
        categoryRepository.existsByUserIdAndCategoryName(userId, categoryName))) {
      throw new ResourceAlreadyExistsException(
          "Category with name '%s' already exists for user '%s'".formatted(categoryName, userId));
    }
  }

  private void checkIfCategoryAlreadyExists(UUID userId, String categoryName, UUID categoryId) {
    if (Boolean.TRUE.equals(
        categoryRepository.existsByUserIdAndCategoryNameAndCategoryIdNot(
            userId, categoryName, categoryId))) {
      throw new ResourceAlreadyExistsException(
          "Category with name '%s' already exists for user '%s'".formatted(categoryName, userId));
    }
  }

  @Override
  @Transactional
  public void deleteCategory(UUID categoryId) {
    Category category = findCategoryById(categoryId);
    if (category.getParentCategory() != null) {
      category.getParentCategory().getSubCategories().remove(category);
    }
    categoryRepository.delete(category);
    categoryEventPublisher.publish(category, EventTypeEnum.DELETE);
    LOGGER.info(
        "Deleted category '{}' for user '{}'", category.getCategoryId(), category.getUserId());
    eventService.publishEvent(
        securityService.getAuthenticatedUserId(),
        CategorySseEventEnum.CATEGORY_DELETED.name(),
        CategoryMapper.toCategoryDto(category));
  }

  @Override
  @Transactional
  public void deleteCategoriesByUserId(UUID userId) {
    userServiceClient.checkIfUserStillExist(userId);
    LOGGER.info("Deleting categories related to user '{}'", userId);
    categoryRepository.deleteAll(categoryRepository.findCategoriesByUserId(userId));
  }

  private Category findCategoryById(UUID categoryId) {
    return categoryRepository
        .findById(categoryId)
        .orElseThrow(
            () ->
                new ResourceNotFoundException(
                    "Category with id %s not found".formatted(categoryId)));
  }
}
