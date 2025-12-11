package it.moneyverse.transaction.services;

import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.core.services.SecurityService;
import it.moneyverse.core.services.SseEventService;
import it.moneyverse.transaction.enums.TagSseEventEnum;
import it.moneyverse.transaction.model.dto.TagDto;
import it.moneyverse.transaction.model.dto.TagRequestDto;
import it.moneyverse.transaction.model.dto.TagUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.repositories.TagRepository;
import it.moneyverse.transaction.model.repositories.TransactionRepository;
import it.moneyverse.transaction.utils.mapper.TagMapper;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagManagementService implements TagService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TagManagementService.class);

  private final TagRepository tagRepository;
  private final TransactionRepository transactionRepository;
  private final SecurityService securityService;
  private final SseEventService eventService;

  public TagManagementService(
      TagRepository tagRepository,
      TransactionRepository transactionRepository,
      SecurityService securityService,
      SseEventService eventService) {
    this.tagRepository = tagRepository;
    this.transactionRepository = transactionRepository;
    this.securityService = securityService;
    this.eventService = eventService;
  }

  @Override
  @Transactional
  public TagDto createTag(TagRequestDto request) {
    checkIfTagAlreadyExist(request.tagName(), request.userId());
    LOGGER.info("Creating tag {} for user {}", request.tagName(), request.userId());
    Tag tag = TagMapper.toTag(request);
    TagDto result = TagMapper.toTagDto(tagRepository.save(tag));
    eventService.publishEvent(
        securityService.getAuthenticatedUserId(),
        TagSseEventEnum.TAG_CREATED.name(),
        result.getTagId());
    return result;
  }

  @Override
  @Transactional(readOnly = true)
  public List<TagDto> getUserTags(UUID userId) {
    LOGGER.info("Getting tags for user {}", userId);
    return TagMapper.toTagDto(tagRepository.findByUserId(userId));
  }

  @Override
  @Transactional(readOnly = true)
  public TagDto getTagById(UUID tagId) {
    LOGGER.info("Getting tag {}", tagId);
    return TagMapper.toTagDto(findTagById(tagId));
  }

  @Override
  public Set<Tag> getTagsByIds(Set<UUID> tagIds) {
    if (tagIds == null || tagIds.isEmpty()) {
      return Collections.emptySet();
    }
    return tagIds.stream().map(this::findTagById).collect(Collectors.toSet());
  }

  @Override
  @Transactional
  public TagDto updateTag(UUID tagId, TagUpdateRequestDto request) {
    Tag tag = findTagById(tagId);
    if (request.tagName() != null
        && tagRepository.existsByTagNameAndUserIdAndTagIdNot(
            request.tagName(), tag.getUserId(), tagId)) {
      throw new ResourceAlreadyExistsException(
          "Tag %s cannot be updated for user %s because another tag with the same name already exists"
              .formatted(request.tagName(), tag.getUserId()));
    }
    LOGGER.info("Updating tag {}", tagId);
    tag = TagMapper.partialUpdate(tag, request);
    TagDto result = TagMapper.toTagDto(tagRepository.save(tag));
    eventService.publishEvent(
        securityService.getAuthenticatedUserId(),
        TagSseEventEnum.TAG_UPDATED.name(),
        result.getTagId());
    return result;
  }

  @Override
  @Transactional
  public void deleteTag(UUID tagId) {
    Tag tag = findTagById(tagId);
    LOGGER.info("Deleting tag {}", tagId);
    tag.getTransactions()
        .forEach(
            transaction -> {
              transaction.getTags().remove(tag);
              transactionRepository.save(transaction);
            });
    tagRepository.delete(tag);
    eventService.publishEvent(
        securityService.getAuthenticatedUserId(),
        TagSseEventEnum.TAG_CREATED.name(),
        tag.getTagId());
  }

  private Tag findTagById(UUID tagId) {
    return tagRepository
        .findById(tagId)
        .orElseThrow(() -> new ResourceNotFoundException("Tag %s does not exist".formatted(tagId)));
  }

  private void checkIfTagAlreadyExist(String tagName, UUID userId) {
    if (tagRepository.existsByTagNameAndUserId(tagName, userId)) {
      throw new ResourceAlreadyExistsException(
          "Tag %s already exists for user %s".formatted(tagName, userId));
    }
  }

  @Override
  @Transactional
  public void deleteAllTagsByUserId(UUID userId) {
    LOGGER.info("Deleting all tags for user {}", userId);
    List<Tag> tags = tagRepository.findByUserId(userId);
    for (Tag tag : tags) {
      tag.getTransactions()
          .forEach(
              transaction -> {
                transaction.getTags().remove(tag);
                transactionRepository.save(transaction);
              });
    }
    tagRepository.deleteAll(tagRepository.findByUserId(userId));
  }
}
