package it.moneyverse.budget.services;

import it.moneyverse.budget.model.dto.CategoryDto;
import it.moneyverse.budget.model.dto.CategoryRequestDto;
import it.moneyverse.budget.model.dto.CategoryUpdateRequestDto;
import it.moneyverse.budget.model.entities.Category;
import it.moneyverse.budget.model.entities.Category_;
import it.moneyverse.budget.model.event.CategoryDeletionEvent;
import it.moneyverse.budget.model.repositories.CategoryRepository;
import it.moneyverse.budget.model.repositories.DefaultCategoryRepository;
import it.moneyverse.budget.utils.mapper.CategoryMapper;
import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.model.beans.CategoryDeletionTopic;
import it.moneyverse.core.model.dto.PageCriteria;
import it.moneyverse.core.services.MessageProducer;
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
  private final MessageProducer<UUID, String> messageProducer;

  public CategoryManagementService(
      CategoryRepository categoryRepository,
      DefaultCategoryRepository defaultCategoryRepository,
      UserServiceClient userServiceClient,
      MessageProducer<UUID, String> messageProducer) {
    this.categoryRepository = categoryRepository;
    this.defaultCategoryRepository = defaultCategoryRepository;
    this.userServiceClient = userServiceClient;
    this.messageProducer = messageProducer;
  }

  @Override
  @Transactional
  public CategoryDto createCategory(CategoryRequestDto request) {
    checkIfUserExists(request.userId());
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
    Pageable pageRequest =
        PageRequest.of(
            pageCriteria.getOffset(),
            pageCriteria.getLimit(),
            Sort.by(Category_.CATEGORY_NAME).ascending());
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
      checkIfCategoryAlreadyExists(category.getUserId(), request.categoryName());
    }
    category =
        request.parentId().isPresent()
            ? updateCategoryWithParent(category, request)
            : CategoryMapper.partialUpdate(category, request);
    CategoryDto result = CategoryMapper.toCategoryDto(categoryRepository.save(category));
    LOGGER.info("Updated category '{}' for user '{}'", categoryId, category.getUserId());
    return result;
  }

  private Category updateCategoryWithParent(Category category, CategoryUpdateRequestDto request) {
    if (!request.parentId().isPresent()) {
      return CategoryMapper.partialUpdate(category, request, null);
    }
    UUID parentId = request.parentId().get();
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

  @Override
  @Transactional
  public void deleteCategory(UUID categoryId) {
    Category category = findCategoryById(categoryId);
    categoryRepository.delete(category);
    messageProducer.send(
        new CategoryDeletionEvent(categoryId, category.getUserId()), CategoryDeletionTopic.TOPIC);
    LOGGER.info(
        "Deleted category '{}' for user '{}'", category.getCategoryId(), category.getUserId());
  }

  @Override
  @Transactional
  public void deleteCategoriesByUserId(UUID userId) {
    checkIfUserExists(userId);
    LOGGER.info("Deleting categories related to user '{}'", userId);
    categoryRepository.deleteAll(categoryRepository.findCategoriesByUserId(userId));
  }

  private void checkIfUserExists(UUID userId) {
    if (Boolean.FALSE.equals(userServiceClient.checkIfUserExists(userId))) {
      throw new ResourceNotFoundException("User %s does not exists".formatted(userId));
    }
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
