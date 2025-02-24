package it.moneyverse.transaction.services;

import it.moneyverse.core.exceptions.ResourceAlreadyExistsException;
import it.moneyverse.core.exceptions.ResourceNotFoundException;
import it.moneyverse.transaction.model.dto.TagDto;
import it.moneyverse.transaction.model.dto.TagRequestDto;
import it.moneyverse.transaction.model.dto.TagUpdateRequestDto;
import it.moneyverse.transaction.model.entities.Tag;
import it.moneyverse.transaction.model.repositories.TagRepository;
import it.moneyverse.transaction.model.repositories.TransactionRepository;
import it.moneyverse.transaction.utils.mapper.TagMapper;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TagManagementService implements TagService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TagManagementService.class);

  private final TagRepository tagRepository;
  private final TransactionRepository transactionRepository;

  public TagManagementService(
      TagRepository tagRepository, TransactionRepository transactionRepository) {
    this.tagRepository = tagRepository;
    this.transactionRepository = transactionRepository;
  }

  @Override
  @Transactional
  public TagDto createTag(TagRequestDto request) {
    checkIfTagAlreadyExist(request.tagName(), request.userId());
    LOGGER.info("Creating tag {} for user {}", request.tagName(), request.userId());
    Tag tag = TagMapper.toTag(request);
    return TagMapper.toTagDto(tagRepository.save(tag));
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
  @Transactional
  public TagDto updateTag(UUID tagId, TagUpdateRequestDto request) {
    Tag tag = findTagById(tagId);
    if (request.tagName() != null) {
      checkIfTagAlreadyExist(request.tagName(), tag.getUserId());
    }
    LOGGER.info("Updating tag {}", tagId);
    tag = TagMapper.partialUpdate(tag, request);
    return TagMapper.toTagDto(tagRepository.save(tag));
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
  }

  private Tag findTagById(UUID tagId) {
    return tagRepository
        .findById(tagId)
        .orElseThrow(() -> new ResourceNotFoundException("Tag %s does not exist".formatted(tagId)));
  }

  private void checkIfTagAlreadyExist(String tagName, UUID userId) {
    if (Boolean.TRUE.equals(tagRepository.existsByTagNameAndUserId(tagName, userId))) {
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
